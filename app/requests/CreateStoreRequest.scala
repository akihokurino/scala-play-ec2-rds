package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl

case class CreateStoreRequest(agencyId: Int,
                              adminUserId: Int,
                              businessConditionId: Int,
                              prefectureId: Int,
                              areaId: Option[Int],
                              requestedDate: String,
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

object CreateStoreRequest {
  val form = Form(mapping(
    "agencyId" -> number,
    "adminUserId" -> number,
    "businessConditionId" -> number,
    "prefectureId" -> number,
    "areaId" -> optional(number),
    "requestedDate" -> nonEmptyText(minLength = 1, maxLength = 10),
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
  )(CreateStoreRequest.apply)(CreateStoreRequest.unapply))
}

