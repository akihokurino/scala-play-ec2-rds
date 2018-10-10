package infrastructure

import models.AdminUser
import models.Question
import org.joda.time.DateTime
import requests.{CreateAnswerRequest, UpdateAnswerRequest}
import scalikejdbc._

case class AnswerDAO(id: Int, adminUserId: Int, questionId: Int, text: String, createdAt: String) {
  def to(adminUserDAO: AdminUserDAO): Question.Answer = Question.Answer(id, adminUserDAO.to(), text, createdAt)
}

object AnswerDAO extends SQLSyntaxSupport[AnswerDAO] {
  override val tableName = "answers"

  def apply(r: ResultName[AnswerDAO])(rs: WrappedResultSet) =
    new AnswerDAO(
      rs.int(r.id),
      rs.int(r.adminUserId),
      rs.int(r.questionId),
      rs.string(r.text),
      rs.string(r.createdAt)
    )

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Question.Answer] = {
    val _answerDAO = AnswerDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(AnswerDAO as _answerDAO)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_answerDAO.adminUserId, _adminUserDAO.id)
        .where
        .eq(_answerDAO.id, id)
    }.map { rs =>
      val answerDAO = AnswerDAO(_answerDAO.resultName)(rs)
      val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)

      answerDAO.to(adminUserDAO)
    }.single().apply()
  }

  def create(question: Question, adminUser: AdminUser, data: CreateAnswerRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(AnswerDAO).namedValues(
        column.adminUserId -> adminUser.id,
        column.questionId -> question.id,
        column.text -> data.text,
        column.createdAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def edit(id: Int, data: UpdateAnswerRequest)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(AnswerDAO).set(
        column.text -> data.text
      ).where.eq(AnswerDAO.column.id, id)
    }.update().apply()

    true
  }
}
