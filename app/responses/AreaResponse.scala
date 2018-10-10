package responses

import models.Area
import spray.json._

case class AreaResponse(id: Int, name: String)

object AreaProtocol extends DefaultJsonProtocol {
  implicit def areaFormat: RootJsonFormat[AreaResponse] = jsonFormat2(AreaResponse.apply)
}

object AreaResponse {
  def from(area: Area): AreaResponse = AreaResponse(area.id, area.name)
}
