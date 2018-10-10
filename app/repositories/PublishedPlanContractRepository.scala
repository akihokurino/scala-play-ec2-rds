package repositories

import com.google.inject.ImplementedBy
import infrastructure.PublishedPlanContractDAO
import models.{PublishedContract, PublishedPlanContract}
import requests.CreatePublishedPlanContractRequest

@ImplementedBy(classOf[PublishedPlanContractRepositoryImpl])
trait PublishedPlanContractRepository {
  def fetchAll(): List[PublishedPlanContract.WithStore]
  def fetchAllOf(status: PublishedContract.Status): List[PublishedPlanContract.WithStore]
  def fetch(id: Int): Option[PublishedPlanContract.WithStore]
  def create(data: CreatePublishedPlanContractRequest): PublishedPlanContract.WithStore
}

class PublishedPlanContractRepositoryImpl extends PublishedPlanContractRepository {
  def fetchAll(): List[PublishedPlanContract.WithStore] = PublishedPlanContractDAO.fetchAll()

  def fetchAllOf(status: PublishedContract.Status): List[PublishedPlanContract.WithStore] = PublishedPlanContractDAO.fetchAllOfStatus(status)

  def fetch(id: Int): Option[PublishedPlanContract.WithStore] = PublishedPlanContractDAO.fetchById(id)

  def create(data: CreatePublishedPlanContractRequest): PublishedPlanContract.WithStore = {
    val insertId = PublishedPlanContractDAO.create(data)
    fetch(insertId).get
  }
}

object PublishedPlanContractRepositoryImpl {

}
