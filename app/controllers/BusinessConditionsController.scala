package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.BusinessConditionRepository
import responses.ResponseJsonProtocol._
import responses.BusinessConditionProtocol._
import responses.{BusinessConditionResponse, ErrorResponse, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class BusinessConditionsController @Inject()(authService: AuthService,
                                             businessConditionRepository: BusinessConditionRepository) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[BusinessConditionResponse](
        authService.encodeJWT(adminUser),
        businessConditionRepository.fetchAll().map({ BusinessConditionResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
