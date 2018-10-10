package infrastructure.intermediate

import scalikejdbc._

case class AgencyAdminUserDAO(agencyId: Int, adminUserId: Int)

object AgencyAdminUserDAO extends SQLSyntaxSupport[AgencyAdminUserDAO] {
  override val tableName = "agency_admin_user"

  def apply(r: ResultName[AgencyAdminUserDAO])(rs: WrappedResultSet) =
    new AgencyAdminUserDAO(
      rs.int(r.agencyId),
      rs.int(r.adminUserId)
    )

  def create(agencyId: Int, adminUserId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      insert.into(AgencyAdminUserDAO).namedValues(
        column.agencyId -> agencyId,
        column.adminUserId -> adminUserId
      )
    }.update().apply()

    true
  }
}