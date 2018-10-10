package responses

import models.PublishedPlanContract
import spray.json._

case class StoreLatestEndDateResponse(endDate: String)

object StoreLatestEndDateProtocol extends DefaultJsonProtocol {
  implicit def storeLatestEndDateFormat: RootJsonFormat[StoreLatestEndDateResponse] = jsonFormat1(StoreLatestEndDateResponse.apply)
}

object StoreLatestEndDateResponse {
  def from(plan: Option[PublishedPlanContract]): StoreLatestEndDateResponse = plan match {
    case Some(p) => StoreLatestEndDateResponse(p.endDate)
    case None => StoreLatestEndDateResponse("")
  }
}

