package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl

case class UpdateStoreRequest(businessConditionId: Int,
                              prefectureId: Int,
                              areaId: Option[Int],
                              name: String,
                              nameKana: String,
                              postalCode: String,
                              address: String,
                              buildingName: String,
                              phoneNumber: String,
                              restaurantPermissionNumber: String,
                              customsPermissionNumber: String,
                              managerName: String,
                              managerNameKana: String,
                              managerEmail: String,
                              managerSubEmail: String)

object UpdateStoreRequest {
  val form = Form(mapping(
    "businessConditionId" -> number,
    "prefectureId" -> number,
    "areaId" -> optional(number),
    "name" -> nonEmptyText(minLength = 1, maxLength = 255),
    "nameKana" -> nonEmptyText(minLength = 1, maxLength = 255),
    "postalCode" -> nonEmptyText(minLength = 1, maxLength = 20),
    "address" -> nonEmptyText(minLength = 1, maxLength = 255),
    "buildingName" -> text(minLength = 0, maxLength = 255),
    "phoneNumber" -> nonEmptyText(minLength = 1, maxLength = 20),
    "restaurantPermissionNumber" -> text(minLength = 0, maxLength = 255),
    "customsPermissionNumber" -> text(minLength = 0, maxLength = 255),
    "managerName" -> nonEmptyText(minLength = 1, maxLength = 255),
    "managerNameKana" -> nonEmptyText(minLength = 1, maxLength = 255),
    "managerEmail" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern),
    "managerSubEmail" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern)
  )(UpdateStoreRequest.apply)(UpdateStoreRequest.unapply))
}