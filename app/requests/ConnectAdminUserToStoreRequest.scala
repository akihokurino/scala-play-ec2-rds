package requests

import play.api.data.Form
import play.api.data.Forms._

case class ConnectAdminUserToStoreRequest(storeId: Int)

object ConnectAdminUserToStoreRequest {
  val form = Form(mapping(
    "storeId" -> number
  )(ConnectAdminUserToStoreRequest.apply)(ConnectAdminUserToStoreRequest.unapply))
}
