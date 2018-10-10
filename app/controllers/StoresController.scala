package controllers

import javax.inject.{Inject, Singleton}

import models.{AdminUser, Store}
import play.api.mvc._
import repositories.StoreRepository
import requests.{CreateNearestStationRequest, CreateStoreRequest, UpdateStoreRequest}
import responses.ResponseJsonProtocol._
import responses.StoreProtocol._
import responses.StoreOverviewProtocol._
import responses.StoreLatestEndDateProtocol._
import responses._
import services.{AuthService, TaskQueueService}
import spray.json._

@Singleton
class StoresController @Inject()(authService: AuthService,
                                 taskQueueService: TaskQueueService,
                                 storeRepository: StoreRepository) extends Controller {

  def index(status: Option[Int]) = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      adminUser.role match  {
        case AdminUser.Role.Master =>
          val stores = status match {
            case Some(id) => storeRepository.fetchAllOf(Store.Status.from(id))
            case None => storeRepository.fetchAll()
          }
          Ok(ResponseResults[StoreResponse](
            authService.encodeJWT(adminUser),
            stores.map({ it => StoreResponse.from(it.self, it.agency, it.adminUser) })).toJson.toString())
        case AdminUser.Role.Agency =>
          val stores = status match {
            case Some(id) => adminUser.agency.get.filterManagedStores(Store.Status.from(id))
            case None => adminUser.agency.get.managedStores
          }
          Ok(ResponseResults[StoreResponse](
            authService.encodeJWT(adminUser),
            stores.map({ it => StoreResponse.from(it.self, it.agency, it.adminUser) })).toJson.toString())
        case AdminUser.Role.Store =>
          val stores = adminUser.ownStores
          Ok(ResponseResults[StoreOverviewResponse](
            authService.encodeJWT(adminUser),
            stores.map({ StoreOverviewResponse.from })).toJson.toString())
      }
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      storeRepository.fetch(id).map { store =>
        Ok(ResponseResult[StoreResponse](
          authService.encodeJWT(adminUser),
          StoreResponse.from(store.self, store.agency, store.adminUser)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def me = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Store)).map { adminUser =>
      adminUser.ownStore.map { store =>
        Ok(ResponseResult[StoreResponse](
          authService.encodeJWT(adminUser),
          StoreResponse.from(store)).toJson.toString())
      }.getOrElse(Unauthorized)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def latestEndDate(id: Int) = Action { implicit  request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      storeRepository.fetch(id).map { store =>
        Ok(ResponseResult[StoreLatestEndDateResponse](
          authService.encodeJWT(adminUser),
          StoreLatestEndDateResponse.from(store.self.latestPlan)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateStoreRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (!storeRepository.isExist(requestData.name)) {
            val store = storeRepository.create(requestData)

            Ok(ResponseResult[StoreResponse](
              authService.encodeJWT(adminUser),
              StoreResponse.from(store.self, store.agency, store.adminUser)).toJson.toString())
          } else {
            Unauthorized(ErrorResponse(ErrorResponse.Message.alreadyExistStoreName).toJson.toString())
          }
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      UpdateStoreRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (!storeRepository.isExist(requestData.name, id)) {
            storeRepository.fetch(id).map { store =>
              Ok(ResponseResult[StoreResponse](
                authService.encodeJWT(adminUser),
                StoreResponse.from(store.self.update(requestData, taskQueueService))).toJson.toString())
            }.getOrElse(NotFound)
          } else {
            Unauthorized(ErrorResponse(ErrorResponse.Message.alreadyExistStoreName).toJson.toString())
          }
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def deleteAllNearestStation(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      storeRepository.fetch(id).map { store =>
        Ok(ResponseResult[StoreResponse](
          authService.encodeJWT(adminUser),
          StoreResponse.from(store.self.deleteAllNearestStation())).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def createNearestStation(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateNearestStationRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          storeRepository.fetch(id).map { store =>
            Ok(ResponseResult[StoreResponse](
              authService.encodeJWT(adminUser),
              StoreResponse.from(store.self.createNearestStation(requestData))).toJson.toString())
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def approve(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      storeRepository.fetch(id).map { store =>
        Ok(ResponseResult[StoreResponse](
          authService.encodeJWT(adminUser),
          StoreResponse.from(store.self.approve(), store.agency, store.adminUser)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def reject(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      storeRepository.fetch(id).map { store =>
        Ok(ResponseResult[StoreResponse](
          authService.encodeJWT(adminUser),
          StoreResponse.from(store.self.reject(), store.agency, store.adminUser)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}