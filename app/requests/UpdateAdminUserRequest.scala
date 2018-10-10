package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl
import utils.HashUtil

case class UpdateAdminUserRequest(username: String, email: String, password: String) {
  val sha1Password: String = HashUtil.encode(password)
}

object UpdateAdminUserRequest {
  val form = Form(mapping(
    "username" -> nonEmptyText(minLength = 1, maxLength = 255),
    "email" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern),
    "password" -> nonEmptyText(minLength = 1, maxLength = 255)
  )(UpdateAdminUserRequest.apply)(UpdateAdminUserRequest.unapply))
}