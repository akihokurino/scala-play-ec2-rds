package infrastructure

import scalikejdbc._

case class RegionDAO(id: Int, name: String, nameKana: String)

object RegionDAO extends SQLSyntaxSupport[RegionDAO] {
  override val tableName = "regions"

  def apply(r: ResultName[RegionDAO])(rs: WrappedResultSet) =
    new RegionDAO(
      rs.int(r.id),
      rs.string(r.name),
      rs.string(r.nameKana)
    )
}