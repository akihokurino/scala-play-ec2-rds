package responses

import models.Recruitment
import responses.RecruitmentInfoProtocol._
import responses.OccupationProtocol._
import responses.RecruitmentPhotoProtocol._
import responses.SpecificTagProtocol._
import responses.RecruitmentDisplayProtocol._
import spray.json._

case class RecruitmentResponse(id: Int,
                               statusId: Int,
                               storeId: Int,
                               statusName: String,
                               display: RecruitmentDisplayResponse,
                               info: RecruitmentInfoResponse,
                               createdAt: String,
                               updatedAt: String,
                               occupations: List[OccupationResponse],
                               photos: List[RecruitmentPhotoResponse],
                               tags: List[SpecificTagResponse])

object RecruitmentProtocol extends DefaultJsonProtocol {
  implicit def recruitmentFormat: RootJsonFormat[RecruitmentResponse] = jsonFormat11(RecruitmentResponse.apply)
}

object RecruitmentResponse {
  def from(recruitment: Recruitment): RecruitmentResponse =
    RecruitmentResponse(
      recruitment.id,
      recruitment.status.id,
      recruitment.storeId,
      recruitment.status.text,
      RecruitmentDisplayResponse.from(recruitment),
      RecruitmentInfoResponse.from(recruitment),
      recruitment.createdAt,
      recruitment.updatedAt,
      recruitment.occupations.map({ OccupationResponse.from }),
      recruitment.photos.map({ RecruitmentPhotoResponse.from }),
      recruitment.tags.map({ SpecificTagResponse.from })
    )
}
