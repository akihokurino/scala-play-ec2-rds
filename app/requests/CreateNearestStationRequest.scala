package requests

import play.api.data.{Form, Mapping}
import play.api.data.Forms._

case class CreateNearestStationRequest(routeId: Int, stationId: Int)

object CreateNearestStationRequest {
  val params: Mapping[CreateNearestStationRequest] = mapping(
    "routeId" -> number,
    "stationId" -> number
  )(CreateNearestStationRequest.apply)(CreateNearestStationRequest.unapply)

  val form = Form(params)
}
