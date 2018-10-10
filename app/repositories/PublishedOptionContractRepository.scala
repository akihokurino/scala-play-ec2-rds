package repositories

import com.google.inject.ImplementedBy
import infrastructure.PublishedOptionContractDAO
import models.{PublishedContract, PublishedOptionContract}
import requests.CreatePublishedOptionContractRequest

@ImplementedBy(classOf[PublishedOptionContractRepositoryImpl])
trait PublishedOptionContractRepository {
  def fetchAll(): List[PublishedOptionContract.WithStore]
  def fetchAllOf(status: PublishedContract.Status): List[PublishedOptionContract.WithStore]
  def fetch(id: Int): Option[PublishedOptionContract.WithStore]
  def create(data: CreatePublishedOptionContractRequest): PublishedOptionContract.WithStore
}

class PublishedOptionContractRepositoryImpl extends PublishedOptionContractRepository {
  def fetchAll(): List[PublishedOptionContract.WithStore] = PublishedOptionContractDAO.fetchAll()

  def fetchAllOf(status: PublishedContract.Status): List[PublishedOptionContract.WithStore] = PublishedOptionContractDAO.fetchAllOfStatus(status)

  def fetch(id: Int): Option[PublishedOptionContract.WithStore] = PublishedOptionContractDAO.fetchById(id)

  def create(data: CreatePublishedOptionContractRequest): PublishedOptionContract.WithStore = {
    val insertId = PublishedOptionContractDAO.create(data)
    fetch(insertId).get
  }
}

object PublishedOptionContractRepositoryImpl {

}
