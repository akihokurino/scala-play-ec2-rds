package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.PrefectureRepository
import responses.ResponseJsonProtocol._
import responses.PrefectureProtocol._
import responses.{ErrorResponse, PrefectureResponse, ResponseResult, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class PrefecturesController @Inject()(authService: AuthService,
                                      prefectureRepository: PrefectureRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[PrefectureResponse](
        authService.encodeJWT(adminUser),
        prefectureRepository.fetchAll().map({ PrefectureResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      prefectureRepository.fetch(id).map { prefecture =>
        Ok(ResponseResult[PrefectureResponse](
          authService.encodeJWT(adminUser),
          PrefectureResponse.from(prefecture)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
