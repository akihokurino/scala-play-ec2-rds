package infrastructure

import scalikejdbc._

case class RecruitmentStatusDAO(id: Int, name: String)

object RecruitmentStatusDAO extends SQLSyntaxSupport[RecruitmentStatusDAO] {
  override val tableName = "recruitment_statuses"

  def apply(rs: WrappedResultSet) =
    new RecruitmentStatusDAO(
      rs.int("id"),
      rs.string("name")
    )
}