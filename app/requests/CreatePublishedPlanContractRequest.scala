package requests

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._

case class CreatePublishedPlanContractRequest(storeId: Int, startDate: String, endDate: String) {
  val isValidDate: Boolean = DateTime.parse(startDate).isBefore(DateTime.parse(endDate))
}

object CreatePublishedPlanContractRequest {
  val form = Form(mapping(
    "storeId" -> number,
    "startDate" -> nonEmptyText(minLength = 1, maxLength = 10),
    "endDate" -> nonEmptyText(minLength = 1, maxLength = 10)
  )(CreatePublishedPlanContractRequest.apply)(CreatePublishedPlanContractRequest.unapply))
}

