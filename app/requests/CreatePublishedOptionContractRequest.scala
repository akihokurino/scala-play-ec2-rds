package requests

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._

case class CreatePublishedOptionContractRequest(storeId: Int, optionId: Int, startDate: String, endDate: String) {
  val isValidDate: Boolean = DateTime.parse(startDate).isBefore(DateTime.parse(endDate))
}

object CreatePublishedOptionContractRequest {
  val form = Form(mapping(
    "storeId" -> number,
    "optionId" -> number,
    "startDate" -> nonEmptyText(minLength = 1, maxLength = 10),
    "endDate" -> nonEmptyText(minLength = 1, maxLength = 10)
  )(CreatePublishedOptionContractRequest.apply)(CreatePublishedOptionContractRequest.unapply))
}

