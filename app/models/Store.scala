package models

import infrastructure.{NearestStationDAO, StoreDAO}
import requests.{CreateNearestStationRequest, UpdateStoreRequest}
import services.TaskQueueService

case class Store(id: Int,
                 businessCondition: BusinessCondition,
                 status: Store.Status,
                 info: Store.Info,
                 manager: Store.Manager,
                 recruitment: Option[Recruitment],
                 tmpRecruitment: Option[TmpRecruitment],
                 requestedDate: String,
                 createdAt: String,
                 updatedAt: String,
                 optionContracts: List[PublishedOptionContract],
                 planContracts: List[PublishedPlanContract]) {

  def update(data: UpdateStoreRequest, taskQueueService: TaskQueueService): Store = {
    StoreDAO.edit(id, data)
    val self = StoreDAO.fetchById(id).get.self
    self.recruitment match {
      case Some(r) => taskQueueService.queueSearchIndex(r)
      case None =>
    }
    self
  }

  def latestPlan: Option[PublishedPlanContract] = {
    val plansWithoutRejected = planContracts.filter({ it =>
      it.status != PublishedContract.Status.Rejected
    })
    plansWithoutRejected.headOption
  }

  def approve(): Store = {
    updateStatus(Store.Status.Approved)
  }

  def reject(): Store = {
    updateStatus(Store.Status.Rejected)
  }

  def createNearestStation(data: CreateNearestStationRequest): Store = {
    val nearestStation = NearestStationDAO.fetchById(NearestStationDAO.create(this.id, data))
    val newInfo = info.copy(nearestStations = info.nearestStations ++ nearestStation)
    copy(info = newInfo)
  }

  def deleteAllNearestStation(): Store = {
    NearestStationDAO.deleteAll(id)
    val newInfo = info.copy(nearestStations = List.empty)
    copy(info = newInfo)
  }

  private def updateStatus(newStatus: Store.Status): Store = {
    StoreDAO.updateStatus(id, newStatus)
    copy(status = newStatus)
  }
}

object Store {

  sealed abstract class Status(val id: Int) {
    val text: String
  }

  object Status {
    def from(id: Int): Status = {
      id match {
        case 1 => Requesting
        case 2 => Rejected
        case 3 => Approved
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object Requesting extends Status(1) {
      override val text: String = "申請中"
    }

    case object Rejected extends Status(2) {
      override val text: String = "拒否"
    }

    case object Approved extends Status(3) {
      override val text: String = "承認済"
    }
  }

  case class Manager(name: String, nameKana: String, email: String, subEmail: String)

  case class Info(name: String,
                  nameKana: String,
                  postalCode: String,
                  prefecture: Prefecture,
                  area: Option[Area],
                  address: String,
                  nearestStations: List[NearestStation],
                  buildingName: String,
                  phoneNumber: String,
                  restaurantPermissionNumber: String,
                  customsPermissionNumber: String)

  case class NearestStation(id: Int, routeId: Int, stationId: Int)

  case class Overview(id: Int, name: String, agency: Agency, adminUser: AdminUser)

  case class WithAdmin(self: Store, agency: Agency, adminUser: AdminUser)
}
