package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.PublishedOptionRepository
import responses.PublishedOptionProtocol._
import responses.ResponseJsonProtocol._
import responses.{ErrorResponse, PublishedOptionResponse, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class PublishedOptionsController @Inject()(authService: AuthService,
                                           publishedOptionRepository: PublishedOptionRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[PublishedOptionResponse](
        authService.encodeJWT(adminUser),
        publishedOptionRepository.fetchAll().map({ PublishedOptionResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
