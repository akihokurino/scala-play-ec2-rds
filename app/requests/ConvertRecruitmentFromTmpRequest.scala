package requests

import play.api.data.Form
import play.api.data.Forms._

case class ConvertRecruitmentFromTmpRequest(tmpId: Int)

object ConvertRecruitmentFromTmpRequest {
  val form = Form(mapping(
    "tmpId" -> number
  )(ConvertRecruitmentFromTmpRequest.apply)(ConvertRecruitmentFromTmpRequest.unapply))
}

