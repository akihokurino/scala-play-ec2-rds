package infrastructure.intermediate

import models.AdminUser
import scalikejdbc._

case class StoreAdminUserDAO(storeId: Int, adminUserId: Int)

object StoreAdminUserDAO extends SQLSyntaxSupport[StoreAdminUserDAO] {
  override val tableName = "store_admin_user"

  def apply(r: ResultName[StoreAdminUserDAO])(rs: WrappedResultSet) =
    new StoreAdminUserDAO(
      rs.int(r.storeId),
      rs.int(r.adminUserId)
    )

  def fetchStoreIdsOfAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): List[Int] = {
    val _storeAdminUserDAO = StoreAdminUserDAO.syntax

    withSQL {
      select(_storeAdminUserDAO.storeId).from(StoreAdminUserDAO as _storeAdminUserDAO)
        .where
        .eq(_storeAdminUserDAO.adminUserId, adminUser.id)
    }.map(_.int(1)).list().apply()
  }

  def create(storeId: Int, adminUserId: Int)(implicit s: DBSession = AutoSession): Boolean = {

    withSQL {
      insert.into(StoreAdminUserDAO).namedValues(
        column.storeId -> storeId,
        column.adminUserId -> adminUserId
      )
    }.update().apply()

    true
  }
}
