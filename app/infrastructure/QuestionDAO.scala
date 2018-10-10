package infrastructure

import models.{Question, User}
import scalikejdbc._

case class QuestionDAO(id: Int, statusId: Int, entryId: Int, text: String, createdAt: String) {
  def to(user: User): Question = Question(id, user, Question.Status.from(statusId), text, createdAt, None)

  def to(user: User, answer: Question.Answer): Question = Question(id, user, Question.Status.from(statusId), text, createdAt, Some(answer))

  val isAnswered: Boolean = Question.Status.from(statusId) == Question.Status.Answered
}

object QuestionDAO extends SQLSyntaxSupport[QuestionDAO] {
  override val tableName = "questions"

  def apply(r: ResultName[QuestionDAO])(rs: WrappedResultSet) =
    new QuestionDAO(
      rs.int(r.id),
      rs.int(r.statusId),
      rs.int(r.entryId),
      rs.string(r.text),
      rs.string(r.createdAt)
    )

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Question] = {
    val _questionDAO = QuestionDAO.syntax
    val _entryDAO = EntryDAO.syntax
    val _userDAO = UserDAO.syntax
    val _answerDAO = AnswerDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(QuestionDAO as _questionDAO)
        .innerJoin(EntryDAO as _entryDAO)
        .on(_questionDAO.entryId, _entryDAO.id)
        .innerJoin(UserDAO as _userDAO)
        .on(_entryDAO.userId, _userDAO.id)
        .leftJoin(AnswerDAO as _answerDAO)
        .on(_questionDAO.id, _answerDAO.questionId)
        .leftJoin(AdminUserDAO as _adminUserDAO)
        .on(_answerDAO.adminUserId, _adminUserDAO.id)
        .where
        .eq(_questionDAO.id, id)
    }.map { rs =>
      val userDAO = UserDAO(_userDAO.resultName)(rs)
      val questionDAO = QuestionDAO(_questionDAO.resultName)(rs)

      if (questionDAO.isAnswered) {
        val answerDAO = AnswerDAO(_answerDAO.resultName)(rs)
        val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)
        questionDAO.to(userDAO.to(), answerDAO.to(adminUserDAO))
      } else {
        questionDAO.to(userDAO.to())
      }
    }.single().apply()
  }

  def updateStatus(question: Question, status: Question.Status)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(QuestionDAO).set(column.statusId -> status.id)
        .where
        .eq(QuestionDAO.column.id, question.id)
    }.update().apply()

    true
  }

  def isExistOfEntryId(entryId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    val _questionDAO = QuestionDAO.syntax

    val result = withSQL {
      select(_questionDAO.id).from(QuestionDAO as _questionDAO)
        .where
        .eq(_questionDAO.entryId, entryId)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }
}