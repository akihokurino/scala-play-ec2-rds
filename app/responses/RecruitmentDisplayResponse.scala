package responses

import models.Recruitment
import spray.json._

case class RecruitmentDisplayResponse(occupationId: Int,
                                      occupationName: String,
                                      paymentTypeId: Int,
                                      paymentTypeName: String,
                                      paymentFrom: Int,
                                      paymentTo: Option[Int])

object RecruitmentDisplayProtocol extends DefaultJsonProtocol {
  implicit def recruitmentDisplayFormat: RootJsonFormat[RecruitmentDisplayResponse] = jsonFormat6(RecruitmentDisplayResponse.apply)
}

object RecruitmentDisplayResponse {
  def from(recruitment: Recruitment): RecruitmentDisplayResponse =
    RecruitmentDisplayResponse(
      recruitment.display.occupation.id,
      recruitment.display.occupation.name,
      recruitment.display.paymentType.id,
      recruitment.display.paymentType.text,
      recruitment.display.paymentFrom,
      recruitment.display.paymentTo
    )
}
