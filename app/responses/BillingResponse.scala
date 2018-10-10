package responses

import models.Billing
import spray.json._
import responses.PublishedContractProtocol._

case class BillingResponse(id: Int, contract: PublishedContractResponse, createdAt: String, updatedAt: String)

object BillingProtocol extends DefaultJsonProtocol {
  implicit def areaFormat: RootJsonFormat[BillingResponse] = jsonFormat4(BillingResponse.apply)
}

object BillingResponse {
  def from(billing: Billing): BillingResponse = BillingResponse(
    billing.id,
    PublishedContractResponse.from(
      billing.contract.self,
      billing.contract.store
    ),
    billing.createdAt,
    billing.updatedAt)
}
