package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import repositories.QuestionRepository
import responses.QuestionProtocol._
import responses.ResponseJsonProtocol._
import responses.{ErrorResponse, QuestionResponse, ResponseResult}
import services.AuthService
import spray.json._

@Singleton
class QuestionsController @Inject()(authService: AuthService,
                                    questionRepository: QuestionRepository) extends Controller {

  def show(id: Int) = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      questionRepository.fetch(id).map { question =>
        Ok(ResponseResult[QuestionResponse](
          authService.encodeJWT(adminUser),
          QuestionResponse.from(question)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}

