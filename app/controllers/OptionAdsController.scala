package controllers

import javax.inject.{Inject, Singleton}

import models.AdminUser
import play.api.mvc._
import repositories.OptionAdRepository
import requests.CreateOptionAdRequest
import responses.OptionAdProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.{AuthService, StorageService}
import spray.json._

@Singleton
class OptionAdsController @Inject()(authService: AuthService,
                                    s3Service: StorageService,
                                    optionAdRepository: OptionAdRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      val ads = adminUser.role match {
        case AdminUser.Role.Master => optionAdRepository.fetchAll()
        case AdminUser.Role.Agency => adminUser.agency.get.managedOptionAds
        case AdminUser.Role.Store => throw new RuntimeException()
      }

      Ok(ResponseResults[OptionAdResponse](
        authService.encodeJWT(adminUser),
        ads.map({ OptionAdResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      CreateOptionAdRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          Ok(ResponseResult[OptionAdResponse](
            authService.encodeJWT(adminUser),
            OptionAdResponse.from(optionAdRepository.create(requestData))).toJson.toString())
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def updateThumbnail(id: Int) = Action(parse.multipartFormData) { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      optionAdRepository.fetch(id).map { ad =>
        request.body.file("file").map { data =>
          Ok(ResponseResult[OptionAdResponse](
            authService.encodeJWT(adminUser),
            OptionAdResponse.from(ad.updateThumbnail(data, s3Service))).toJson.toString())
        }.getOrElse(BadRequest(ErrorResponse(ErrorResponse.Message.unExistUploadFile).toJson.toString()))
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def delete(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      optionAdRepository.fetch(id).map { ad =>
        ad.delete()
        Ok(ResponseToken(authService.encodeJWT(adminUser)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}