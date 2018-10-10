package responses

import models.Agency
import spray.json._
import responses.AdminUserProtocol._

case class AgencyResponse(id: Int, name: String, adminUsers: List[AdminUserResponse])

object AgencyProtocol extends DefaultJsonProtocol {
  implicit def agencyFormat: RootJsonFormat[AgencyResponse] = jsonFormat3(AgencyResponse.apply)
}

object AgencyResponse {
  def from(agency: Agency): AgencyResponse =
    AgencyResponse(
      agency.id,
      agency.name,
      agency.adminUsers.map(AdminUserResponse.from)
    )
}