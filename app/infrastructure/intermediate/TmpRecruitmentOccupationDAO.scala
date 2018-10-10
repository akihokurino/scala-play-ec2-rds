package infrastructure.intermediate

import scalikejdbc._

case class TmpRecruitmentOccupationDAO(tmpRecruitmentId: Int, occupationId: Int)

object TmpRecruitmentOccupationDAO extends SQLSyntaxSupport[TmpRecruitmentOccupationDAO] {
  override val tableName = "tmp_recruitment_occupation"

  def apply(r: ResultName[TmpRecruitmentOccupationDAO])(rs: WrappedResultSet) =
    new TmpRecruitmentOccupationDAO(
      rs.int(r.tmpRecruitmentId),
      rs.int(r.occupationId)
    )

  def create(recruitmentId: Int, occupationId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      insert.into(TmpRecruitmentOccupationDAO).namedValues(
        column.tmpRecruitmentId -> recruitmentId,
        column.occupationId -> occupationId
      )
    }.update().apply()

    true
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(TmpRecruitmentOccupationDAO).where.eq(column.tmpRecruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}