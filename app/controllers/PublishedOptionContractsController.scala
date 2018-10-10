package controllers

import javax.inject.{Inject, Singleton}

import models.{AdminUser, PublishedContract}
import play.api.mvc._
import repositories.{BillingRepository, PublishedOptionContractRepository}
import requests.{CreatePublishedOptionContractRequest, UpdatePublishedOptionContractRequest}
import responses.PublishedContractProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.AuthService
import spray.json._

@Singleton
class PublishedOptionContractsController @Inject()(authService: AuthService,
                                                   publishedOptionContractRepository: PublishedOptionContractRepository,
                                                   billingRepository: BillingRepository) extends Controller {

  def index(status: Option[Int]) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      val contracts = adminUser.role match {
        case AdminUser.Role.Master => {
          status.map({ id =>
            publishedOptionContractRepository.fetchAllOf(PublishedContract.Status.from(id))
          }).getOrElse(
            publishedOptionContractRepository.fetchAll()
          )
        }
        case AdminUser.Role.Agency => adminUser.agency.get.managedOptionContracts
        case AdminUser.Role.Store => throw new RuntimeException()
      }

      Ok(ResponseResults[PublishedContractResponse](
        authService.encodeJWT(adminUser),
        contracts.map({ it => PublishedContractResponse.from(it.self, it.store) })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      publishedOptionContractRepository.fetch(id).map { contract =>
        Ok(ResponseResult[PublishedContractResponse](
          authService.encodeJWT(adminUser),
          PublishedContractResponse.from(contract.self, contract.store)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreatePublishedOptionContractRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (requestData.isValidDate) {
            val contract = publishedOptionContractRepository.create(requestData)

            Ok(ResponseResult[PublishedContractResponse](
              authService.encodeJWT(adminUser),
              PublishedContractResponse.from(contract.self, contract.store)).toJson.toString())
          } else {
            BadRequest(ErrorResponse(ErrorResponse.Message.invalidContractDate).toJson.toString())
          }
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      UpdatePublishedOptionContractRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          if (requestData.isValidDate) {
            publishedOptionContractRepository.fetch(id).map { contract =>
              Ok(ResponseResult[PublishedContractResponse](
                authService.encodeJWT(adminUser),
                PublishedContractResponse.from(contract.self.update(requestData), contract.store)).toJson.toString())
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
      publishedOptionContractRepository.fetch(id).map { contract =>
        if (contract.self.status != PublishedContract.Status.Closed) {
          val _contract = contract.self.approve()
          val _ = billingRepository.create(_contract)
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
      publishedOptionContractRepository.fetch(id).map { contract =>
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
