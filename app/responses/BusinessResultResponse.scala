package responses

import models.AdminUser
import spray.json._
import responses.AdminUserProtocol._

case class BusinessResultResponse(adminUser: AdminUserResponse, storeCount: Int, recruitmentCount: Int, salesAmount: Int)

object BusinessResultProtocol extends DefaultJsonProtocol {
  implicit def calcBusinessResultFormat: RootJsonFormat[BusinessResultResponse] = jsonFormat4(BusinessResultResponse.apply)
}

object BusinessResultResponse {
  def from(businessResult: AdminUser.BusinessResult): BusinessResultResponse =
    BusinessResultResponse(
      AdminUserResponse.from(businessResult.adminUser),
      businessResult.storeCount,
      businessResult.recruitmentCount,
      businessResult.salesAmount
    )
}