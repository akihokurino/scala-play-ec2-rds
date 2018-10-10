package infrastructure.intermediate

import scalikejdbc._

case class TmpRecruitmentSpecificTagDAO(tmpRecruitmentId: Int, specificTagId: Int)

object TmpRecruitmentSpecificTagDAO extends SQLSyntaxSupport[TmpRecruitmentSpecificTagDAO] {
  override val tableName = "tmp_recruitment_specific_tag"

  def apply(r: ResultName[TmpRecruitmentSpecificTagDAO])(rs: WrappedResultSet) =
   new TmpRecruitmentSpecificTagDAO(
      rs.int(r.tmpRecruitmentId),
      rs.int(r.specificTagId)
    )

  def create(recruitmentId: Int, tagId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      insert.into(TmpRecruitmentSpecificTagDAO).namedValues(
        column.tmpRecruitmentId -> recruitmentId,
        column.specificTagId -> tagId
      )
    }.update().apply()

    true
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(TmpRecruitmentSpecificTagDAO).where.eq(column.tmpRecruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}