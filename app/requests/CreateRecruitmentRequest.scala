package requests

import play.api.data.Form
import play.api.data.Forms._

case class CreateRecruitmentRequest(storeId: Int,
                                    displayOccupationId: Int,
                                    displayPaymentTypeId: Int,
                                    displayPaymentFrom: Int,
                                    displayPaymentTo: Option[Int],
                                    title: String,
                                    pr: String,
                                    workInfo: String,
                                    paymentInfo: String,
                                    workingHoursInfo: String,
                                    holidayInfo: String,
                                    requirementInfo: String,
                                    treatmentInfo: String,
                                    entryMethodInfo: String,
                                    lineUrl: String,
                                    occupationIds: List[Int],
                                    specificTagIds: List[Int])

object CreateRecruitmentRequest {
  val form = Form(mapping(
    "storeId" -> number,
    "displayOccupationId" -> number,
    "displayPaymentTypeId" -> number,
    "displayPaymentFrom" -> number,
    "displayPaymentTo" -> optional(number),
    "title" -> nonEmptyText(minLength = 1, maxLength = 255),
    "pr" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "workInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "paymentInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "workingHoursInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "holidayInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "requirementInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "treatmentInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "entryMethodInfo" -> nonEmptyText(minLength = 1, maxLength = 1000),
    "lineUrl" -> text(minLength = 0, maxLength = 255),
    "occupationIds" -> list(number),
    "specificTagIds" -> list(number)
  )(CreateRecruitmentRequest.apply)(CreateRecruitmentRequest.unapply))
}

