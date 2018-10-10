package infrastructure.intermediate

import scalikejdbc._

case class RecruitmentSpecificTagDAO(recruitmentId: Int, specificTagId: Int)

object RecruitmentSpecificTagDAO extends SQLSyntaxSupport[RecruitmentSpecificTagDAO] {
  override val tableName = "recruitment_specific_tag"

  def apply(r: ResultName[RecruitmentSpecificTagDAO])(rs: WrappedResultSet) =
    new RecruitmentSpecificTagDAO(
      rs.int(r.recruitmentId),
      rs.int(r.specificTagId)
    )

  def create(recruitmentId: Int, tagId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      insert.into(RecruitmentSpecificTagDAO).namedValues(
        column.recruitmentId -> recruitmentId,
        column.specificTagId -> tagId
      )
    }.update().apply()

    true
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(RecruitmentSpecificTagDAO).where.eq(column.recruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}