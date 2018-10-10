package models

import infrastructure.{PublishedPlanContractDAO, StoreDAO}
import requests.UpdatePublishedPlanContractRequest

case class PublishedPlanContract(id: Int,
                                 contractType: PublishedContract.Type,
                                 status: PublishedContract.Status,
                                 plan: PublishedPlan,
                                 startDate: String,
                                 endDate: String,
                                 billingAmount: Int,
                                 createdAt: String,
                                 updatedAt: String) extends PublishedContract {

  override val name: String = plan.name

  def update(data: UpdatePublishedPlanContractRequest): PublishedPlanContract = {
    PublishedPlanContractDAO.edit(id, data)
    PublishedPlanContract(
      id,
      contractType,
      status,
      plan,
      data.startDate,
      data.endDate,
      data.billingAmount,
      createdAt,
      updatedAt
    )
  }

  def approve(): PublishedPlanContract = {
    PublishedPlanContractDAO.updateStatus(id, PublishedContract.Status.Approved)
    PublishedPlanContract(
      id,
      contractType,
      PublishedContract.Status.Approved,
      plan,
      startDate,
      endDate,
      billingAmount,
      createdAt,
      updatedAt)
  }

  def reject(): PublishedPlanContract = {
    PublishedPlanContractDAO.updateStatus(id, PublishedContract.Status.Rejected)
    PublishedPlanContract(
      id,
      contractType,
      PublishedContract.Status.Rejected,
      plan,
      startDate,
      endDate,
      billingAmount,
      createdAt,
      updatedAt)
  }
}

object PublishedPlanContract {

  case class WithStore(self: PublishedPlanContract, store: Store.Overview) {
    val isEditable: Boolean = {
      StoreDAO.fetchById(store.id).exists({ store =>
        store.self.latestPlan match {
          case Some(p) => p.id == self.id
          case None => false
        }
      })
    }
  }
}
