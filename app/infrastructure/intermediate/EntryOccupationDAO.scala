package infrastructure.intermediate

import scalikejdbc._

case class EntryOccupationDAO(entryId: Int, occupationId: Int)

object EntryOccupationDAO extends SQLSyntaxSupport[EntryOccupationDAO] {
  override val tableName = "entry_occupation"

  def apply(r: ResultName[EntryOccupationDAO])(rs: WrappedResultSet) =
    new EntryOccupationDAO(
      rs.int(r.entryId),
      rs.int(r.occupationId)
    )
}
