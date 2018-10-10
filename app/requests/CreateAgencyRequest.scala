package requests

import play.api.data.Form
import play.api.data.Forms._

case class CreateAgencyRequest(name: String)

object CreateAgencyRequest {
  val form = Form(mapping(
    "name" -> nonEmptyText(minLength = 1, maxLength = 255)
  )(CreateAgencyRequest.apply)(CreateAgencyRequest.unapply))
}