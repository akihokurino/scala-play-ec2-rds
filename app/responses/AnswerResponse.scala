package responses

import models.Question
import responses.AdminUserProtocol._
import spray.json._

case class AnswerResponse(id: Int,
                          from: AdminUserResponse,
                          text: String,
                          createdAt: String)

object AnswerProtocol extends DefaultJsonProtocol {
  implicit def answerFormat: RootJsonFormat[AnswerResponse] = jsonFormat4(AnswerResponse.apply)
}

object AnswerResponse {
  def from(answer: Question.Answer): AnswerResponse =
    AnswerResponse(
      answer.id,
      AdminUserResponse.from(answer.from),
      answer.text,
      answer.createdAt
    )
}