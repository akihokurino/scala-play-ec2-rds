package requests

import play.api.data.Form
import play.api.data.Forms._
import services.AuthServiceImpl
import utils.HashUtil

case class SignInRequest(email: String, password: String) {
  val sha1Password: String = HashUtil.encode(password)
}

object SignInRequest {
  val form = Form(mapping(
    "email" -> nonEmptyText.verifying(AuthServiceImpl.emailPattern),
    "password" -> nonEmptyText(minLength = 1, maxLength = 255)
  )(SignInRequest.apply)(SignInRequest.unapply))
}
