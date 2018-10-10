package responses

import models.AdminUser
import spray.json._

case class AdminUserResponse(id: Int, username: String, email: String, roleId: Int, storeId: Option[Int])

object AdminUserProtocol extends DefaultJsonProtocol {
  implicit def adminUserFormat: RootJsonFormat[AdminUserResponse] = jsonFormat5(AdminUserResponse.apply)
}

object AdminUserResponse {
  def from(user: AdminUser): AdminUserResponse =
    AdminUserResponse(
      user.id,
      user.username,
      user.email,
      user.role.id,
      user.storeId
    )
}