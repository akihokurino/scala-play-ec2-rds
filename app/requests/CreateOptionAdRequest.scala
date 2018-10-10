package requests

import play.api.data.Form
import play.api.data.Forms._

case class CreateOptionAdRequest(storeId: Int, optionId: Int, occupationId: Int, startDate: String, endDate: String)

object CreateOptionAdRequest {
  val form = Form(mapping(
    "storeId" -> number,
    "optionId" -> number,
    "occupationId" -> number,
    "startDate" -> nonEmptyText(minLength = 1, maxLength = 10),
    "endDate" -> nonEmptyText(minLength = 1, maxLength = 10)
  )(CreateOptionAdRequest.apply)(CreateOptionAdRequest.unapply))
}