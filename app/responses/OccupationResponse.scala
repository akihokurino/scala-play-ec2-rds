package responses

import models.Occupation
import spray.json._

case class OccupationResponse(id: Int, name: String)

object OccupationProtocol extends DefaultJsonProtocol {
  implicit def occupationFormat: RootJsonFormat[OccupationResponse] = jsonFormat2(OccupationResponse.apply)
}

object OccupationResponse {
  def from(occupation: Occupation): OccupationResponse = OccupationResponse(occupation.id, occupation.name)
}
