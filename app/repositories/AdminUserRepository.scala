package repositories

import com.google.inject.ImplementedBy
import infrastructure.AdminUserDAO
import models.AdminUser
import requests.{CreateAgencyAdminUserRequest, CreateStoreAdminUserRequest, SignInRequest}

@ImplementedBy(classOf[AdminUserRepositoryImpl])
trait AdminUserRepository {
  def fetchAllStoreOwner(): List[AdminUser]
  def fetchAllStoreOwnerOf(storeId: Int): List[AdminUser]
  def fetchAllStoreOwnerOf(storeIds: List[Int]): List[AdminUser]
  def fetch(id: Int): Option[AdminUser]
  def authenticate(data: SignInRequest): Option[AdminUser]
  def create(data: CreateAgencyAdminUserRequest): AdminUser
  def create(data: CreateStoreAdminUserRequest): AdminUser
  def isExist(email: String): Boolean
}

class AdminUserRepositoryImpl extends AdminUserRepository {
  def fetchAllStoreOwner(): List[AdminUser] = AdminUserDAO.fetchAllStoreOwner()

  def fetchAllStoreOwnerOf(storeId: Int): List[AdminUser] = AdminUserDAO.fetchAllStoreOwnerOfStoreId(storeId)

  def fetchAllStoreOwnerOf(storeIds: List[Int]): List[AdminUser] = AdminUserDAO.fetchAllStoreOwnerOfStoreIds(storeIds)

  def fetch(id: Int): Option[AdminUser] = AdminUserDAO.fetchById(id)

  def authenticate(data: SignInRequest): Option[AdminUser] = AdminUserDAO.authenticate(data)

  def create(data: CreateAgencyAdminUserRequest): AdminUser = {
    val insertId = AdminUserDAO.createOfAgency(data)
    fetch(insertId).get
  }

  def create(data: CreateStoreAdminUserRequest): AdminUser = {
    val insertId = AdminUserDAO.createOfStore(data)
    fetch(insertId).get
  }

  def isExist(email: String): Boolean = AdminUserDAO.isExistThisEmail(email)
}

object AdminUserRepositoryImpl {

}
