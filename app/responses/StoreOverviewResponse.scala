package responses

import models.Store
import responses.AgencyProtocol._
import responses.AdminUserProtocol._
import responses.PrefectureProtocol._
import responses.BusinessConditionProtocol._
import spray.json._

case class StoreOverviewResponse(id: Int,
                                 name: String,
                                 agency: Option[AgencyResponse],
                                 adminUser: Option[AdminUserResponse],
                                 prefecture: Option[PrefectureResponse],
                                 businessCondition: Option[BusinessConditionResponse])

object StoreOverviewProtocol extends DefaultJsonProtocol {
  implicit def storeOverviewFormat: RootJsonFormat[StoreOverviewResponse] = jsonFormat6(StoreOverviewResponse.apply)
}

object StoreOverviewResponse {
  def from(store: Store.Overview): StoreOverviewResponse =
    StoreOverviewResponse(
      store.id,
      store.name,
      Some(AgencyResponse.from(store.agency)),
      Some(AdminUserResponse.from(store.adminUser)),
      None,
      None
    )

  def from(store: Store.WithAdmin): StoreOverviewResponse =
    StoreOverviewResponse(
      store.self.id,
      store.self.info.name,
      Some(AgencyResponse.from(store.agency)),
      Some(AdminUserResponse.from(store.adminUser)),
      None,
      None
    )

  def from(store: Store): StoreOverviewResponse =
    StoreOverviewResponse(
      store.id,
      store.info.name,
      None,
      None,
      Some(PrefectureResponse.from(store.info.prefecture)),
      Some(BusinessConditionResponse.from(store.businessCondition))
    )
}
