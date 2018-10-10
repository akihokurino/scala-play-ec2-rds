package responses

import models.Recruitment
import spray.json._

case class RecruitmentPhotoResponse(id: Int, resourceName: String)

object RecruitmentPhotoProtocol extends DefaultJsonProtocol {
  implicit def recruitmentPhotoFormat: RootJsonFormat[RecruitmentPhotoResponse] = jsonFormat2(RecruitmentPhotoResponse.apply)
}

object RecruitmentPhotoResponse {
  def from(photo: Recruitment.Photo): RecruitmentPhotoResponse = RecruitmentPhotoResponse(photo.id, photo.resourceFullPath)
}
