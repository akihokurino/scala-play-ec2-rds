package controllers

import javax.inject.{Inject, Singleton}

import models.AdminUser
import play.api.mvc._
import repositories.AgencyRepository
import requests.CreateAgencyRequest
import responses.AgencyBusinessResultProtocol._
import responses.AgencyProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.AuthService
import spray.json._

@Singleton
class AgenciesController @Inject()(authService: AuthService,
                                   agencyRepository: AgencyRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      Ok(ResponseResults[AgencyResponse](
        authService.encodeJWT(adminUser),
        agencyRepository.fetchAll().map(AgencyResponse.from)).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
     agencyRepository.fetch(id).map { agency =>
       Ok(ResponseResult[AgencyResponse](
         authService.encodeJWT(adminUser),
         AgencyResponse.from(agency)).toJson.toString())
     }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      CreateAgencyRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          val agency = agencyRepository.create(requestData)

          Ok(ResponseResult[AgencyResponse](
            authService.encodeJWT(adminUser),
            AgencyResponse.from(agency)).toJson.toString())
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def me = Action { implicit  request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      adminUser.agency.map { agency =>
        Ok(ResponseResult[AgencyResponse](
          authService.encodeJWT(adminUser),
          AgencyResponse.from(agency)).toJson.toString())
      }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.notAuthorityGetAgency).toJson.toString()))
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def businessResults = Action { implicit  request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      adminUser.agency.map { agency =>
        Ok(ResponseResult[AgencyBusinessResultResponse](
          authService.encodeJWT(adminUser),
          AgencyBusinessResultResponse.from(agency.calcBusinessResults())).toJson.toString())
      }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.notAuthorityGetAgency).toJson.toString()))
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
