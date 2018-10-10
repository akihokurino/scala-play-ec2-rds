package repositories

import com.google.inject.ImplementedBy
import infrastructure.StoreDAO
import models.Store
import requests.CreateStoreRequest

@ImplementedBy(classOf[StoreRepositoryImpl])
trait StoreRepository {
  def fetchAll(): List[Store.WithAdmin]
  def fetchAllOf(status: Store.Status): List[Store.WithAdmin]
  def fetch(id: Int): Option[Store.WithAdmin]
  def isExist(name: String): Boolean
  def isExist(name: String, excludeId: Int): Boolean
  def isExistTmpRecruitment(id: Int): Boolean
  def create(data: CreateStoreRequest): Store.WithAdmin
}

class StoreRepositoryImpl extends StoreRepository {
  def fetchAll(): List[Store.WithAdmin] = StoreDAO.fetchAll()

  def fetchAllOf(status: Store.Status): List[Store.WithAdmin] = StoreDAO.fetchAllOfStatus(status)

  def fetch(id: Int): Option[Store.WithAdmin] = StoreDAO.fetchById(id)

  def isExist(name: String): Boolean = StoreDAO.isExistThisName(name)

  def isExist(name: String, excludeId: Int): Boolean = StoreDAO.isExistThisNameExclude(name, excludeId)

  def isExistTmpRecruitment(id: Int): Boolean = StoreDAO.isExistTmpRecruitment(id)

  def create(data: CreateStoreRequest): Store.WithAdmin = {
    val insertId = StoreDAO.create(data)
    fetch(insertId).get
  }
}

object StoreRepositoryImpl {

}
