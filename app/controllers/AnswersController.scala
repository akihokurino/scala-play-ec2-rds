package controllers

import javax.inject.{Inject, Singleton}

import models.AdminUser
import play.api.mvc._
import repositories.QuestionRepository
import requests.{CreateAnswerRequest, UpdateAnswerRequest}
import responses.QuestionProtocol._
import responses.ResponseJsonProtocol._
import responses.{ErrorResponse, FormErrorResponse, QuestionResponse, ResponseResult}
import services.AuthService
import spray.json._

@Singleton
class AnswersController @Inject()(authService: AuthService,
                                  questionRepository: QuestionRepository) extends Controller {

  def create(questionId: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Store)).map { adminUser =>
      CreateAnswerRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          questionRepository.fetch(questionId).map { question =>
            Ok(ResponseResult[QuestionResponse](
              authService.encodeJWT(adminUser),
              QuestionResponse.from(question.answer(adminUser, requestData))).toJson.toString())
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(questionId: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      UpdateAnswerRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          questionRepository.fetch(questionId).flatMap({ question =>
            question.answer match {
              case Some(_) => Some(question)
              case None => None
            }
          }).map { question =>
            Ok(ResponseResult[QuestionResponse](
              authService.encodeJWT(adminUser),
              QuestionResponse.from(question.updateAnswer(requestData))).toJson.toString())
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}