package requests

import play.api.data.Form
import play.api.data.Forms._

case class UpdateAnswerRequest(text: String)

object UpdateAnswerRequest {
  val form = Form(mapping(
    "text" -> nonEmptyText(minLength = 1, maxLength = 1000)
  )(UpdateAnswerRequest.apply)(UpdateAnswerRequest.unapply))
}