package infrastructure

import infrastructure.intermediate.{AgencyAdminUserDAO, StoreAdminUserDAO}
import models.{AdminUser, Agency}
import requests._
import scalikejdbc._
import sqls.distinct

case class AdminUserDAO(id: Int,
                        username: String,
                        email: String,
                        password: String,
                        roleId: Int) {

  def to(): AdminUser = AdminUser(id, username, email, AdminUser.Role.from(roleId), None)
}

object AdminUserDAO extends SQLSyntaxSupport[AdminUserDAO] {
  override val tableName = "admin_users"

  def apply(r: ResultName[AdminUserDAO])(rs: WrappedResultSet) =
    new AdminUserDAO(
      rs.int(r.id),
      rs.string(r.username),
      rs.string(r.email),
      rs.string(r.password),
      rs.int(r.roleId)
    )

  def fetchAllManagerOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax
    val _agencyAdminUserDAO = AgencyAdminUserDAO.syntax

    withSQL {
      select.from(AgencyAdminUserDAO as _agencyAdminUserDAO)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_agencyAdminUserDAO.adminUserId, _adminUserDAO.id)
        .where
        .eq(_agencyAdminUserDAO.agencyId, agency.id)
        .orderBy(_adminUserDAO.id)
        .desc
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllStoreOwner()(implicit s: DBSession = AutoSession): List[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax
    val _storeAdminUserDAO = StoreAdminUserDAO.syntax

    val targetIds = withSQL {
      select(distinct(_storeAdminUserDAO.adminUserId))
        .from(StoreAdminUserDAO as _storeAdminUserDAO)
    }.map(_.int(1)).list().apply()

    withSQL {
      select.from(AdminUserDAO as _adminUserDAO)
        .where
        .in(_adminUserDAO.id, targetIds)
        .orderBy(_adminUserDAO.id)
        .desc
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllStoreOwnerOfStoreId(storeId: Int)(implicit s: DBSession = AutoSession): List[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax
    val _storeAdminUserDAO = StoreAdminUserDAO.syntax

    withSQL {
      select.from(StoreAdminUserDAO as _storeAdminUserDAO)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_storeAdminUserDAO.adminUserId, _adminUserDAO.id)
        .where
        .eq(_storeAdminUserDAO.storeId, storeId)
        .orderBy(_adminUserDAO.id)
        .desc
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllStoreOwnerOfStoreIds(storeIds: List[Int])(implicit s: DBSession = AutoSession): List[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax
    val _storeAdminUserDAO = StoreAdminUserDAO.syntax

    withSQL {
      select.from(StoreAdminUserDAO as _storeAdminUserDAO)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_storeAdminUserDAO.adminUserId, _adminUserDAO.id)
        .where
        .in(_storeAdminUserDAO.storeId, storeIds)
        .orderBy(_adminUserDAO.id)
        .desc
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(AdminUserDAO as _adminUserDAO)
        .where
        .eq(_adminUserDAO.id, id)
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.single().apply()
  }

  def authenticate(data: SignInRequest)(implicit s: DBSession = AutoSession): Option[AdminUser] = {
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(AdminUserDAO as _adminUserDAO)
        .where
        .eq(_adminUserDAO.email, data.email)
        .and
        .eq(_adminUserDAO.password, data.sha1Password)
    }.map { rs =>
      AdminUserDAO(_adminUserDAO.resultName)(rs).to()
    }.single().apply()
  }

  def createOfAgency(data: CreateAgencyAdminUserRequest)(implicit s: DBSession = AutoSession): Int = {
    DB localTx { implicit s =>
      val adminUserId = withSQL {
        insert.into(AdminUserDAO).namedValues(
          column.username -> data.username,
          column.email -> data.email,
          column.password -> data.sha1Password,
          column.roleId -> AdminUser.Role.Agency.id
        )
      }.updateAndReturnGeneratedKey().apply().toInt

      AgencyAdminUserDAO.create(data.agencyId, adminUserId)

      adminUserId
    }
  }

  def createOfStore(data: CreateStoreAdminUserRequest)(implicit s: DBSession = AutoSession): Int = {
    DB localTx { implicit s =>
      val adminUserId = withSQL {
        insert.into(AdminUserDAO).namedValues(
          column.username -> data.username,
          column.email -> data.email,
          column.password -> data.sha1Password,
          column.roleId -> AdminUser.Role.Store.id
        )
      }.updateAndReturnGeneratedKey().apply().toInt

      StoreAdminUserDAO.create(data.storeId, adminUserId)

      adminUserId
    }
  }

  def edit(id: Int, data: UpdateAdminUserRequest)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(AdminUserDAO).set(
        column.username -> data.username,
        column.email -> data.email,
        column.password -> data.sha1Password
      ).where.eq(AdminUserDAO.column.id, id)
    }.update().apply()

    true
  }

  def updatePassword(id: Int, data: UpdateAdminUserPasswordRequest)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(AdminUserDAO).set(
        column.password -> data.sha1NewPassword
      ).where.eq(AdminUserDAO.column.id, id)
    }.update().apply()

    true
  }

  def isExistThisEmail(email: String)(implicit s: DBSession = AutoSession): Boolean = {
    val _adminUserDAO = AdminUserDAO.syntax

    val result = withSQL {
      select(_adminUserDAO.id).from(AdminUserDAO as _adminUserDAO)
        .where
        .eq(_adminUserDAO.email, email)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }
}
