package models

import infrastructure.PublishedOptionContractDAO
import requests.UpdatePublishedOptionContractRequest

case class PublishedOptionContract(id: Int,
                                   contractType: PublishedContract.Type,
                                   status: PublishedContract.Status,
                                   option: PublishedOption,
                                   startDate: String,
                                   endDate: String,
                                   billingAmount: Int,
                                   createdAt: String,
                                   updatedAt: String) extends PublishedContract {

  override val name: String = option.name

  def update(data: UpdatePublishedOptionContractRequest): PublishedOptionContract = {
    PublishedOptionContractDAO.edit(id, data)
    copy(startDate = data.startDate, endDate = data.endDate, billingAmount = data.billingAmount)
  }

  def approve(): PublishedOptionContract = updateStatus(PublishedContract.Status.Approved)

  def reject(): PublishedOptionContract = updateStatus(PublishedContract.Status.Rejected)

  private def updateStatus(newStatus: PublishedContract.Status): PublishedOptionContract = {
    PublishedOptionContractDAO.updateStatus(id, newStatus)
    copy(status = newStatus)
  }
}

object PublishedOptionContract {
  case class WithStore(self: PublishedOptionContract, store: Store.Overview)
}
