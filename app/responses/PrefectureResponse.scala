package responses

import models.Prefecture
import spray.json._
import responses.AreaProtocol._

case class PrefectureResponse(id: Int, name: String, areas: List[AreaResponse])

object PrefectureProtocol extends DefaultJsonProtocol {
  implicit def prefectureFormat: RootJsonFormat[PrefectureResponse] = jsonFormat3(PrefectureResponse.apply)
}

object PrefectureResponse {
  def from(prefecture: Prefecture): PrefectureResponse =
    PrefectureResponse(prefecture.id, prefecture.name, prefecture.areas.map({ AreaResponse.from }))
}
