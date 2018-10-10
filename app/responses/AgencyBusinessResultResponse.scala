package responses

import models.Agency
import spray.json._

case class AgencyBusinessResultResponse(totalAmount: Int)

object AgencyBusinessResultProtocol extends DefaultJsonProtocol {
  implicit def agencyBusinessResultFormat: RootJsonFormat[AgencyBusinessResultResponse] = jsonFormat1(AgencyBusinessResultResponse.apply)
}

object AgencyBusinessResultResponse {
  def from(result: Agency.BusinessResult): AgencyBusinessResultResponse = AgencyBusinessResultResponse(result.totalAmount)
}