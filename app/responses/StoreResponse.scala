package responses

import models.{AdminUser, Agency, Store}
import responses.RecruitmentProtocol._
import responses.PublishedContractProtocol._
import responses.StoreInfoProtocol._
import responses.StoreManagerProtocol._
import responses.AdminUserProtocol._
import responses.AgencyProtocol._
import spray.json._

case class StoreResponse(id: Int,
                         businessConditionId: Int,
                         businessConditionName: String,
                         statusId: Int,
                         statusName: String,
                         requestedDate: String,
                         info: StoreInfoResponse,
                         manager: StoreManagerResponse,
                         createdAt: String,
                         updatedAt: String,
                         recruitment: Option[RecruitmentResponse],
                         tmpRecruitment: Option[RecruitmentResponse],
                         optionContracts: List[PublishedContractResponse],
                         planContracts: List[PublishedContractResponse],
                         agency: Option[AgencyResponse],
                         adminUser: Option[AdminUserResponse])

object StoreProtocol extends DefaultJsonProtocol {
  implicit def storeFormat: RootJsonFormat[StoreResponse] = jsonFormat16(StoreResponse.apply)
}

object StoreResponse {
  def from(store: Store): StoreResponse =
    StoreResponse(
      store.id,
      store.businessCondition.id,
      store.businessCondition.name,
      store.status.id,
      store.status.text,
      store.requestedDate,
      StoreInfoResponse.from(store),
      StoreManagerResponse.from(store),
      store.createdAt,
      store.updatedAt,
      store.recruitment.map({ RecruitmentResponse.from }),
      store.tmpRecruitment.map({ it => RecruitmentResponse.from(it.base) }),
      store.optionContracts.map({ PublishedContractResponse.from }),
      store.planContracts.map({ PublishedContractResponse.from }),
      None,
      None
    )

  def from(store: Store, agency: Agency, adminUser: AdminUser): StoreResponse =
    StoreResponse(
      store.id,
      store.businessCondition.id,
      store.businessCondition.name,
      store.status.id,
      store.status.text,
      store.requestedDate,
      StoreInfoResponse.from(store),
      StoreManagerResponse.from(store),
      store.createdAt,
      store.updatedAt,
      store.recruitment.map({ RecruitmentResponse.from }),
      store.tmpRecruitment.map({ it => RecruitmentResponse.from(it.base) }),
      store.optionContracts.map({ PublishedContractResponse.from }),
      store.planContracts.map({ PublishedContractResponse.from }),
      Some(AgencyResponse.from(agency)),
      Some(AdminUserResponse.from(adminUser))
    )
}
