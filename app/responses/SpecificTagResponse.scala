package responses

import models.SpecificTag
import spray.json._

case class SpecificTagResponse(id: Int, name: String)

object SpecificTagProtocol extends DefaultJsonProtocol {
  implicit def specificTagFormat: RootJsonFormat[SpecificTagResponse] = jsonFormat2(SpecificTagResponse.apply)
}

object SpecificTagResponse {
  def from(tag: SpecificTag): SpecificTagResponse = SpecificTagResponse(tag.id, tag.name)
}