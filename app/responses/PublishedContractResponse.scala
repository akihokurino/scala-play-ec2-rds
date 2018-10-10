package responses

import models.{PublishedContract, PublishedOptionContract, PublishedPlanContract, Store}
import responses.StoreOverviewProtocol._
import spray.json._

case class PublishedContractResponse(id: Int,
                                     statusId: Int,
                                     statusName: String,
                                     name: String,
                                     startDate: String,
                                     endDate: String,
                                     createdAt: String,
                                     updatedAt: String,
                                     billingAmount: Int,
                                     store: Option[StoreOverviewResponse],
                                     optionId: Option[Int])

object PublishedContractProtocol extends DefaultJsonProtocol {
  implicit def publishedContractFormat: RootJsonFormat[PublishedContractResponse] = jsonFormat11(PublishedContractResponse.apply)
}

object PublishedContractResponse {
  def from(contract: PublishedOptionContract): PublishedContractResponse =
    PublishedContractResponse(
      contract.id,
      contract.status.id,
      contract.status.text,
      contract.option.name,
      contract.startDate,
      contract.endDate,
      contract.createdAt,
      contract.updatedAt,
      contract.billingAmount,
      None,
      Some(contract.option.id)
    )

  def from(contract: PublishedOptionContract, store: Store.WithAdmin): PublishedContractResponse =
    PublishedContractResponse(
      contract.id,
      contract.status.id,
      contract.status.text,
      contract.option.name,
      contract.startDate,
      contract.endDate,
      contract.createdAt,
      contract.updatedAt,
      contract.billingAmount,
      Some(StoreOverviewResponse.from(store)),
      Some(contract.option.id)
    )

  def from(contract: PublishedPlanContract): PublishedContractResponse =
    PublishedContractResponse(
      contract.id,
      contract.status.id,
      contract.status.text,
      contract.plan.name,
      contract.startDate,
      contract.endDate,
      contract.createdAt,
      contract.updatedAt,
      contract.billingAmount,
      None,
      None
    )

  def from(contract: PublishedContract, store: Store.WithAdmin): PublishedContractResponse =
    PublishedContractResponse(
      contract.id,
      contract.status.id,
      contract.status.text,
      contract.name,
      contract.startDate,
      contract.endDate,
      contract.createdAt,
      contract.updatedAt,
      contract.billingAmount,
      Some(StoreOverviewResponse.from(store)),
      None
    )

  def from(contract: PublishedContract, store: Store.Overview): PublishedContractResponse =
    PublishedContractResponse(
      contract.id,
      contract.status.id,
      contract.status.text,
      contract.name,
      contract.startDate,
      contract.endDate,
      contract.createdAt,
      contract.updatedAt,
      contract.billingAmount,
      Some(StoreOverviewResponse.from(store)),
      None
    )
}
