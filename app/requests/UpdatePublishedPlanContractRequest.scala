package requests

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._

case class UpdatePublishedPlanContractRequest(billingAmount: Int, startDate: String, endDate: String) {
  val isValidDate: Boolean = DateTime.parse(startDate).isBefore(DateTime.parse(endDate))
}

object UpdatePublishedPlanContractRequest {
  val form = Form(mapping(
    "billingAmount" -> number,
    "startDate" -> nonEmptyText(minLength = 1, maxLength = 10),
    "endDate" -> nonEmptyText(minLength = 1, maxLength = 10)
  )(UpdatePublishedPlanContractRequest.apply)(UpdatePublishedPlanContractRequest.unapply))
}