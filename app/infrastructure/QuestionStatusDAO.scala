package infrastructure

import scalikejdbc._

case class QuestionStatusDAO(id: Int, name: String)

object QuestionStatusDAO extends SQLSyntaxSupport[QuestionStatusDAO] {
  override val tableName = "question_statuses"

  def apply(r: ResultName[QuestionStatusDAO])(rs: WrappedResultSet) =
    new QuestionStatusDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}

