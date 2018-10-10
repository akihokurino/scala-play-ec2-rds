package models

import infrastructure.{AnswerDAO, QuestionDAO}
import requests.{CreateAnswerRequest, UpdateAnswerRequest}

case class Question(id: Int,
                    from: User,
                    status: Question.Status,
                    text: String,
                    createdAt: String,
                    answer: Option[Question.Answer]) {

  def answer(adminUser: AdminUser, data: CreateAnswerRequest): Question = {
    val newAnswer = AnswerDAO.fetchById(AnswerDAO.create(this, adminUser, data)).get

    QuestionDAO.updateStatus(this, Question.Status.Answered)
    copy(status = Question.Status.Answered, answer = Some(newAnswer))
  }

  def updateAnswer(data: UpdateAnswerRequest): Question = copy(answer = Some(answer.get.update(data)))
}

object Question {
  sealed abstract class Status(val id: Int) {
    val text: String
  }

  object Status {
    def from(id: Int): Status = {
      id match {
        case 1 => Unanswered
        case 2 => Answered
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object Unanswered extends Status(1) {
      override val text: String = "未回答"
    }
    case object Answered extends Status(2) {
      override val text: String = "回答済"
    }
  }

  case class Answer(id: Int, from: AdminUser, text: String, createdAt: String) {
    def update(data: UpdateAnswerRequest): Answer = {
      AnswerDAO.edit(id, data)
      copy(text = data.text)
    }
  }
}
