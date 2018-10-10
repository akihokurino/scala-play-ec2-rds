package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.OccupationRepository
import responses.ResponseJsonProtocol._
import responses.OccupationProtocol._
import responses.{ErrorResponse, OccupationResponse, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class OccupationsController @Inject()(authService: AuthService,
                                      occupationRepository: OccupationRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[OccupationResponse](
        authService.encodeJWT(adminUser),
        occupationRepository.fetchAll().map({ OccupationResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
