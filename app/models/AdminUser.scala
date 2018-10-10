package models

import infrastructure._
import infrastructure.intermediate.StoreAdminUserDAO
import requests._

case class AdminUser(id: Int,
                     username: String,
                     email: String,
                     role: AdminUser.Role,
                     storeId: Option[Int]) {

  def agency: Option[Agency] = if (role != AdminUser.Role.Agency) {
    None
  } else {
    AgencyDAO.fetchByAdminUser(this)
  }

  def managedStores: List[Store.Overview] = StoreDAO.fetchAllOfAdminUser(this)

  def ownStores: List[Store.Overview] = if (role != AdminUser.Role.Store) {
    List.empty
  } else {
    val ids = StoreAdminUserDAO.fetchStoreIdsOfAdminUser(this)
    StoreDAO.fetchAllOfStoreIds(ids)
  }

  def ownStore: Option[Store] = if (role != AdminUser.Role.Store) {
    None
  } else {
    storeId match {
      case Some(_id) => StoreDAO.fetchById(_id).map({ it => it.self })
      case None => None
    }
  }

  def ownRecruitment: Option[Recruitment] = if (role != AdminUser.Role.Store) {
    None
  } else {
    storeId match {
      case Some(_id) => RecruitmentDAO.fetchByStoreId(_id)
      case None => None
    }
  }

  def ownEntries: List[Entry] = if (role != AdminUser.Role.Store) {
    List.empty
  } else {
    storeId match {
      case Some(_id) => EntryDAO.fetchAllOfStoreId(_id)
      case None => List.empty
    }
  }

  def update(data: UpdateAdminUserRequest): AdminUser = {
    AdminUserDAO.edit(id, data)
    AdminUser(id, data.username, data.email, role, storeId)
  }

  def update(data: UpdateAdminUserPasswordRequest): AdminUser = {
    AdminUserDAO.updatePassword(id, data)
    this
  }

  def authenticateStore(storeId: Int): Option[AdminUser] = {
    val ids = StoreAdminUserDAO.fetchStoreIdsOfAdminUser(this)
    if (ids.contains(storeId)) {
      Some(AdminUser(id, username, email, role, Some(storeId)))
    } else {
      None
    }
  }

  def connectToStore(storeId: Int): Boolean = StoreAdminUserDAO.create(storeId, id)

  def checkUpdateAuthority(updateUserId: Int): Boolean = {
    val managedStoreIds = managedStores.map({ it => it.id })
    val ownerIds = AdminUserDAO.fetchAllStoreOwnerOfStoreIds(managedStoreIds).map({ it => it.id })
    ownerIds.contains(updateUserId)
  }
}

object AdminUser {

  sealed abstract class Role(val id: Int)

  object Role {
    def from(id: Int): Role =
      if (id == 1) {
        Master
      } else if (id == 2) {
        Agency
      } else if (id == 3) {
        Store
      } else {
        throw new RuntimeException("invalid value")
      }


    case object Master extends Role(1)

    case object Agency extends Role(2)

    case object Store extends Role(3)
  }

  case class BusinessResult(adminUser: AdminUser, storeCount: Int, recruitmentCount: Int, salesAmount: Int)
}
