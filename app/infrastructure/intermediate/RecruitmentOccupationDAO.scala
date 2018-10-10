package infrastructure.intermediate

import scalikejdbc._

case class RecruitmentOccupationDAO(recruitmentId: Int, occupationId: Int)

object RecruitmentOccupationDAO extends SQLSyntaxSupport[RecruitmentOccupationDAO] {
  override val tableName = "recruitment_occupation"

  def apply(r: ResultName[RecruitmentOccupationDAO])(rs: WrappedResultSet) =
    new RecruitmentOccupationDAO(
      rs.int(r.recruitmentId),
      rs.int(r.occupationId)
    )

  def create(recruitmentId: Int, occupationId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      insert.into(RecruitmentOccupationDAO).namedValues(
        column.recruitmentId -> recruitmentId,
        column.occupationId -> occupationId
      )
    }.update().apply()

    true
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(RecruitmentOccupationDAO).where.eq(column.recruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}