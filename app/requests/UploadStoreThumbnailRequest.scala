package requests

import play.api.data.Form
import play.api.data.Forms._

case class UploadStoreThumbnailRequest(base64Encode: String)

object UploadStoreThumbnailRequest {
  val form = Form(mapping(
    "base64Encode" -> nonEmptyText
  )(UploadStoreThumbnailRequest.apply)(UploadStoreThumbnailRequest.unapply))
}
