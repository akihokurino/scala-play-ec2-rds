package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl
import utils.HashUtil

case class CreateAgencyAdminUserRequest(username: String, email: String, password: String, agencyId: Int) {
  val sha1Password: String = HashUtil.encode(password)
}

object CreateAgencyAdminUserRequest {
  val form = Form(mapping(
    "username" -> nonEmptyText(minLength = 1, maxLength = 255),
    "email" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern),
    "password" -> nonEmptyText(minLength = 1, maxLength = 255),
    "agencyId" -> number
  )(CreateAgencyAdminUserRequest.apply)(CreateAgencyAdminUserRequest.unapply))
}
