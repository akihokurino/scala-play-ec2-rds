package responses

import models.Recruitment
import spray.json._

case class RecruitmentInfoResponse(title: String,
                                   pr: String,
                                   workInfo: String,
                                   paymentInfo: String,
                                   workingHoursInfo: String,
                                   holidayInfo: String,
                                   requirementInfo: String,
                                   treatmentInfo: String,
                                   entryMethodInfo: String,
                                   lineUrl: String)

object RecruitmentInfoProtocol extends DefaultJsonProtocol {
  implicit def recruitmentInfoFormat: RootJsonFormat[RecruitmentInfoResponse] = jsonFormat10(RecruitmentInfoResponse.apply)
}

object RecruitmentInfoResponse {
  def from(recruitment: Recruitment): RecruitmentInfoResponse =
    RecruitmentInfoResponse(
      recruitment.info.title,
      recruitment.info.pr,
      recruitment.info.work,
      recruitment.info.payment,
      recruitment.info.workingHours,
      recruitment.info.holiday,
      recruitment.info.requirement,
      recruitment.info.treatment,
      recruitment.info.entryMethod,
      recruitment.info.lineUrl
    )
}