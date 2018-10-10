package controllers

import javax.inject.{Inject, Singleton}

import models.{AdminUser, PublishedContract}
import org.joda.time.DateTime
import play.api.mvc._
import repositories.{BillingRepository, PublishedPlanContractRepository, StoreRepository}
import requests.{CreatePublishedPlanContractRequest, UpdatePublishedPlanContractRequest}
import responses.PublishedContractProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.AuthService
import spray.json._

@Singleton
class PublishedPlanContractsController @Inject()(authService: AuthService,
                                                 publishedPlanContractRepository: PublishedPlanContractRepository,
                                                 storeRepository: StoreRepository,
                                                 billingRepository: BillingRepository) extends Controller {

  def index(status: Option[Int]) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      val contracts = adminUser.role match {
        case AdminUser.Role.Master => {
          status.map({ id =>
            publishedPlanContractRepository.fetchAllOf(PublishedContract.Status.from(id))
          }).getOrElse(publishedPlanContractRepository.fetchAll())
        }
        case AdminUser.Role.Agency => throw new RuntimeException()
        case AdminUser.Role.Store => throw new RuntimeException()
      }

      Ok(ResponseResults[PublishedContractResponse](
        authService.encodeJWT(adminUser),
        contracts.map({ it => PublishedContractResponse.from(it.self, it.store) })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      publishedPlanContractRepository.fetch(id).map { contract =>
        Ok(ResponseResult[PublishedContractResponse](
          authService.encodeJWT(adminUser),
          PublishedContractResponse.from(contract.self, contract.store)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def checkEditable(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      publishedPlanContractRepository.fetch(id).map { contract =>
        Ok(ResponseBoolean(
          authService.encodeJWT(adminUser),
          contract.isEditable).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreatePublishedPlanContractRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (requestData.isValidDate) {
            storeRepository.fetch(requestData.storeId).flatMap { store =>
              store.self.latestPlan match {
                case Some(p) => if (DateTime.parse(requestData.startDate).isAfter(DateTime.parse(p.endDate))) Some(store) else None
                case None => Some(store)
              }
            }.map { _ =>
              val contract = publishedPlanContractRepository.create(requestData)

              Ok(ResponseResult[PublishedContractResponse](
                authService.encodeJWT(adminUser),
                PublishedContractResponse.from(contract.self, contract.store)).toJson.toString())
            }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidDateForCreatePlanContract).toJson.toString()))
          } else {
            BadRequest(ErrorResponse(ErrorResponse.Message.invalidContractDate).toJson.toString())
          }
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      UpdatePublishedPlanContractRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (requestData.isValidDate) {
            publishedPlanContractRepository.fetch(id).map { contract =>
              if (DateTime.parse(requestData.startDate).isBefore(DateTime.parse(contract.self.startDate))) {
                Unauthorized(ErrorResponse(ErrorResponse.Message.invalidDateForUpdatePlanContract).toJson.toString())
              } else {
                Ok(ResponseResult[PublishedContractResponse](
                  authService.encodeJWT(adminUser),
                  PublishedContractResponse.from(contract.self.update(requestData), contract.store)).toJson.toString())
              }
            }.getOrElse(NotFound)
          } else {
            BadRequest(ErrorResponse(ErrorResponse.Message.invalidContractDate).toJson.toString())
          }
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def approve(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      publishedPlanContractRepository.fetch(id).map { contract =>
        if (contract.self.status != PublishedContract.Status.Closed) {
          val _contract = contract.self.approve()
//          val _ = billingRepository.create(_contract)
          Ok(ResponseResult[PublishedContractResponse](
            authService.encodeJWT(adminUser),
            PublishedContractResponse.from(_contract, contract.store)).toJson.toString())
        } else {
          Unauthorized(ErrorResponse(ErrorResponse.Message.notUpdateClosedContractStatus).toJson.toString())
        }
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def reject(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      publishedPlanContractRepository.fetch(id).map { contract =>
        if (contract.self.status != PublishedContract.Status.Closed) {
          val _contract = contract.self.reject()
          val _ = billingRepository.delete(_contract)
          Ok(ResponseResult[PublishedContractResponse](
            authService.encodeJWT(adminUser),
            PublishedContractResponse.from(_contract, contract.store)).toJson.toString())
        } else {
          Unauthorized(ErrorResponse(ErrorResponse.Message.notUpdateClosedContractStatus).toJson.toString())
        }
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
