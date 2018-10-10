package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.SpecificTagRepository
import responses.ResponseJsonProtocol._
import responses.SpecificTagProtocol._
import responses.{ErrorResponse, ResponseResults, SpecificTagResponse}
import services.AuthService
import spray.json._

@Singleton
class SpecificTagsController @Inject()(authService: AuthService,
                                       specificTagRepository: SpecificTagRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[SpecificTagResponse](
        authService.encodeJWT(adminUser),
        specificTagRepository.fetchAll().map({ SpecificTagResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
