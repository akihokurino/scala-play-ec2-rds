package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl
import utils.HashUtil

case class UpdateAdminUserPasswordRequest(email: String, oldPassword: String, newPassword: String) {
  val sha1NewPassword: String = HashUtil.encode(newPassword)
}

object UpdateAdminUserPasswordRequest {
  val form = Form(mapping(
    "email" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern),
    "oldPassword" -> nonEmptyText(minLength = 1, maxLength = 255),
    "newPassword" -> nonEmptyText(minLength = 1, maxLength = 255)
  )(UpdateAdminUserPasswordRequest.apply)(UpdateAdminUserPasswordRequest.unapply))
}