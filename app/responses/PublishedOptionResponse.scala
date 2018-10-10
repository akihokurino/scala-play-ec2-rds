package responses

import models.PublishedOption
import spray.json._

case class PublishedOptionResponse(id: Int, name: String)

object PublishedOptionProtocol extends DefaultJsonProtocol {
  implicit def publishedOptionFormat: RootJsonFormat[PublishedOptionResponse] = jsonFormat2(PublishedOptionResponse.apply)
}

object PublishedOptionResponse {
  def from(option: PublishedOption): PublishedOptionResponse = PublishedOptionResponse(option.id, option.name)
}
