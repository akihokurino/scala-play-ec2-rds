package infrastructure

import scalikejdbc._

case class AdminRoleDAO(id: Int, name: String)

object AdminRoleDAO extends SQLSyntaxSupport[AdminRoleDAO] {
  override val tableName = "admin_roles"

  def apply(r: ResultName[AdminRoleDAO])(rs: WrappedResultSet) =
    new AdminRoleDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}