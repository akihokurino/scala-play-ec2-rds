package responses

import models.Store
import spray.json._
import responses.NearestStationProtocol._

case class StoreInfoResponse(name: String,
                             nameKana: String,
                             postalCode: String,
                             address: String,
                             nearestStations: List[NearestStationResponse],
                             buildingName: String,
                             phoneNumber: String,
                             restaurantPermissionNumber: String,
                             customsPermissionNumber: String,
                             prefectureId: Int,
                             prefectureName: String,
                             areaId: Option[Int],
                             areaName: Option[String])

object StoreInfoProtocol extends DefaultJsonProtocol {
  implicit def storeInfoFormat: RootJsonFormat[StoreInfoResponse] = jsonFormat13(StoreInfoResponse.apply)
}

object StoreInfoResponse {
  def from(store: Store): StoreInfoResponse =
    StoreInfoResponse(
      store.info.name,
      store.info.nameKana,
      store.info.postalCode,
      store.info.address,
      store.info.nearestStations.map { NearestStationResponse.from },
      store.info.buildingName,
      store.info.phoneNumber,
      store.info.restaurantPermissionNumber,
      store.info.customsPermissionNumber,
      store.info.prefecture.id,
      store.info.prefecture.name,
      store.info.area.map { it => it.id },
      store.info.area.map { it => it.name })
}
