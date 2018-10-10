package controllers

import javax.inject.{Inject, Singleton}

import models.Recruitment
import play.api.mvc._
import responses.ResponseJsonProtocol._
import responses.PaymentTypeProtocol._
import responses.{ErrorResponse, PaymentTypeResponse, ResponseResults}
import services.AuthService
import spray.json._

@Singleton
class PaymentTypesController @Inject()(authService: AuthService) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[PaymentTypeResponse](
        authService.encodeJWT(adminUser),
        Recruitment.paymentTypes.map({ PaymentTypeResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
