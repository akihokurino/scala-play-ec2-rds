package controllers

import javax.inject.{Inject, Singleton}

import models.AdminUser
import play.api.mvc._
import repositories.PublishContractRepository
import responses.PublishedContractProtocol._
import responses.ResponseJsonProtocol._
import responses.{ErrorResponse, PublishedContractResponse, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class PublishedContractsController @Inject()(authService: AuthService,
                                             publishContractRepository: PublishContractRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      Ok(ResponseResults[PublishedContractResponse](
        authService.encodeJWT(adminUser),
        publishContractRepository.fetchAll().map({ it => PublishedContractResponse.from(it.self, it.store) })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
