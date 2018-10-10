package requests

import play.api.data.Form
import play.api.data.Forms._

case class StoreSignInRequest(storeId: Int)

object StoreSignInRequest {
  val form = Form(mapping(
    "storeId" -> number
  )(StoreSignInRequest.apply)(StoreSignInRequest.unapply))
}
