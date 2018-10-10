package responses

import models.OptionAd
import spray.json._
import responses.PublishedOptionProtocol._
import responses.OccupationProtocol._
import responses.StoreOverviewProtocol._

case class OptionAdResponse(id: Int,
                            store: StoreOverviewResponse,
                            option: PublishedOptionResponse,
                            occupation: OccupationResponse,
                            resourceName: String,
                            startDate: String,
                            endDate: String,
                            createdAt: String)

object OptionAdProtocol extends DefaultJsonProtocol {
  implicit def optionAdFormat: RootJsonFormat[OptionAdResponse] = jsonFormat8(OptionAdResponse.apply)
}

object OptionAdResponse {
  def from(ad: OptionAd): OptionAdResponse =
    OptionAdResponse(
      ad.id,
      StoreOverviewResponse.from(ad.store),
      PublishedOptionResponse.from(ad.option),
      OccupationResponse.from(ad.occupation),
      ad.resourceFullPath,
      ad.startDate,
      ad.endDate,
      ad.createdAt)
}

