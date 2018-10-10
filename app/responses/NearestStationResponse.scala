package responses

import models.Store
import spray.json._

case class NearestStationResponse(id: Int, routeId: Int, stationId: Int)

object NearestStationProtocol extends DefaultJsonProtocol {
  implicit def nearestStationFormat: RootJsonFormat[NearestStationResponse] = jsonFormat3(NearestStationResponse.apply)
}

object NearestStationResponse {
  def from(station: Store.NearestStation): NearestStationResponse = NearestStationResponse(station.id, station.routeId, station.stationId)
}