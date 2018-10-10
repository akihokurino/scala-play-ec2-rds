package infrastructure

import infrastructure.intermediate.AgencyAdminUserDAO
import models.{AdminUser, Agency}
import requests.CreateAgencyRequest
import scalikejdbc._

case class AgencyDAO(id: Int, name: String) {
  def to(): Agency = Agency(id, name, List.empty)

  def to(adminUsers: List[AdminUser]): Agency = Agency(id, name, adminUsers)
}

object AgencyDAO extends SQLSyntaxSupport[AgencyDAO] {
  override val tableName = "agencies"

  def apply(r: ResultName[AgencyDAO])(rs: WrappedResultSet) =
    new AgencyDAO(
      rs.int(r.id),
      rs.string(r.name)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[Agency] = {
    val _agencyDAO = AgencyDAO.syntax

    withSQL {
      select.from(AgencyDAO as _agencyDAO)
        .orderBy(_agencyDAO.id)
        .asc
    }.map { rs =>
      AgencyDAO(_agencyDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Agency] = {
    val _agencyDAO = AgencyDAO.syntax

    withSQL {
      select.from(AgencyDAO as _agencyDAO)
        .where
        .eq(_agencyDAO.id, id)
    }.map { rs =>
      val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
      val adminUsers = AdminUserDAO.fetchAllManagerOfAgency(agencyDAO.to())
      agencyDAO.to(adminUsers)
    }.single().apply()
  }

  def fetchByAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): Option[Agency] = {
    val _agencyDAO = AgencyDAO.syntax
    val _agencyAdminUserDAO = AgencyAdminUserDAO.syntax

    withSQL {
      select.from(AgencyAdminUserDAO as _agencyAdminUserDAO)
        .innerJoin(AgencyDAO as _agencyDAO)
        .on(_agencyAdminUserDAO.agencyId, _agencyDAO.id)
        .where
        .eq(_agencyAdminUserDAO.adminUserId, adminUser.id)
    }.map { rs =>
      AgencyDAO(_agencyDAO.resultName)(rs).to()
    }.single().apply()
  }

  def create(data: CreateAgencyRequest)(implicit s: DBSession = AutoSession): Int = {
    withSQL {
      insert.into(AgencyDAO).namedValues(
        column.name -> data.name
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }
}