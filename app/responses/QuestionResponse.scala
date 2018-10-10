package responses

import models.Question
import responses.UserProtocol._
import responses.AnswerProtocol._
import spray.json._

case class QuestionResponse(id: Int,
                            from: UserResponse,
                            statusId: Int,
                            statusName: String,
                            text: String,
                            createdAt: String,
                            answer: Option[AnswerResponse])

object QuestionProtocol extends DefaultJsonProtocol {
  implicit def questionFormat: RootJsonFormat[QuestionResponse] = jsonFormat7(QuestionResponse.apply)
}

object QuestionResponse {
  def from(question: Question): QuestionResponse =
    QuestionResponse(
      question.id,
      UserResponse.from(question.from),
      question.status.id,
      question.status.text,
      question.text,
      question.createdAt,
      question.answer match {
        case Some(answer) => Some(AnswerResponse.from(answer))
        case None => None
      }
    )
}