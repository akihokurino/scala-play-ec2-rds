package infrastructure

import scalikejdbc._

case class StoreStatusDAO(id: Int, name: String)

object StoreStatusDAO extends SQLSyntaxSupport[StoreStatusDAO] {
  override val tableName = "store_statuses"

  def apply(r: ResultName[StoreStatusDAO])(rs: WrappedResultSet) =
    new StoreStatusDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}