package requests

import play.api.data.Form
import play.api.data.Forms._

case class CreateAnswerRequest(text: String)

object CreateAnswerRequest {
  val form = Form(mapping(
    "text" -> nonEmptyText(minLength = 1, maxLength = 1000)
  )(CreateAnswerRequest.apply)(CreateAnswerRequest.unapply))
}