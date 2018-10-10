package controllers

import javax.inject._

import models.{AdminUser, Recruitment}
import play.api.mvc._
import repositories.{RecruitmentRepository, StoreRepository, TmpRecruitmentRepository}
import requests.{ConvertRecruitmentFromTmpRequest, CreateRecruitmentRequest, UpdateRecruitmentRequest}
import responses.RecruitmentPhotoProtocol._
import responses.RecruitmentProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.{AuthService, StorageService, TaskQueueService}
import spray.json._

@Singleton
class RecruitmentsController @Inject()(authService: AuthService,
                                       taskQueueService: TaskQueueService,
                                       s3Service: StorageService,
                                       recruitmentRepository: RecruitmentRepository,
                                       tmpRecruitmentRepository: TmpRecruitmentRepository,
                                       storeRepository: StoreRepository) extends Controller {

  def index(status: Option[Int]) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      status.map({ id =>
        val recruitments = recruitmentRepository.fetchAllOf(Recruitment.Status.from(id))
        Ok(ResponseResults[RecruitmentResponse](
          authService.encodeJWT(adminUser),
          recruitments.map(RecruitmentResponse.from)).toJson.toString())
      }).getOrElse(BadRequest)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def me = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Store)).map { adminUser =>
      adminUser.ownRecruitment.map { recruitment =>
        Ok(ResponseResult[RecruitmentResponse](
          authService.encodeJWT(adminUser),
          RecruitmentResponse.from(recruitment)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def show(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master, AdminUser.Role.Agency)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        Ok(ResponseResult[RecruitmentResponse](
          authService.encodeJWT(adminUser),
          RecruitmentResponse.from(recruitment)).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def create = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateRecruitmentRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          storeRepository.fetch(requestData.storeId).map { store =>
            store.self.recruitment match {
              case Some(_) =>
                Unauthorized(ErrorResponse(ErrorResponse.Message.alreadyExistRecruitment).toJson.toString())
              case None =>
                Ok(ResponseResult[RecruitmentResponse](
                  authService.encodeJWT(adminUser),
                  RecruitmentResponse.from(recruitmentRepository.create(requestData))).toJson.toString())
            }
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def createTmp(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateRecruitmentRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          recruitmentRepository.fetch(id).map { recruitment =>
            if (recruitment.status == Recruitment.Status.Open) {
              if (storeRepository.isExistTmpRecruitment(recruitment.storeId)) {
                tmpRecruitmentRepository.fetchOfStore(recruitment.storeId) match {
                  case Some(tmp) => tmp.deleteAll()
                  case None =>
                }
              }

              Ok(ResponseResult[RecruitmentResponse](
                authService.encodeJWT(adminUser),
                RecruitmentResponse.from(tmpRecruitmentRepository.create(requestData).base)).toJson.toString())
            } else {
              Unauthorized(ErrorResponse(ErrorResponse.Message.notOpenForCreateTmp).toJson.toString())
            }
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      UpdateRecruitmentRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          recruitmentRepository.fetch(id).map { recruitment =>
            Ok(ResponseResult[RecruitmentResponse](
              authService.encodeJWT(adminUser),
              RecruitmentResponse.from(recruitment.update(requestData, taskQueueService))).toJson.toString())
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def approve(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        if (!recruitment.isFinished) {
          Ok(ResponseResult[RecruitmentResponse](
            authService.encodeJWT(adminUser),
            RecruitmentResponse.from(recruitment.approve())).toJson.toString())
        } else {
          Unauthorized(ErrorResponse(ErrorResponse.Message.notUpdateClosedRecruitmentStatus).toJson.toString())
        }
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def reject(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        if (!recruitment.isFinished) {
          Ok(ResponseResult[RecruitmentResponse](
            authService.encodeJWT(adminUser),
            RecruitmentResponse.from(recruitment.reject())).toJson.toString())
        } else {
          Unauthorized(ErrorResponse(ErrorResponse.Message.notUpdateClosedRecruitmentStatus).toJson.toString())
        }
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def earlyClose(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        if (!recruitment.isFinished) {
          Ok(ResponseResult[RecruitmentResponse](
            authService.encodeJWT(adminUser),
            RecruitmentResponse.from(recruitment.earlyClose())).toJson.toString())
        } else {
          Unauthorized(ErrorResponse(ErrorResponse.Message.notUpdateClosedRecruitmentStatus).toJson.toString())
        }
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def createPhoto(id: Int) = Action(parse.multipartFormData) { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        request.body.file("file").map { data =>
          Ok(ResponseResult[RecruitmentPhotoResponse](
            authService.encodeJWT(adminUser),
            RecruitmentPhotoResponse.from(recruitment.createPhoto(data, s3Service))).toJson.toString())
        }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.unExistUploadFile).toJson.toString()))
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def createTmpPhoto(id: Int) = Action(parse.multipartFormData) { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      tmpRecruitmentRepository.fetch(id).map { recruitment =>
        request.body.file("file").map { data =>
          Ok(ResponseResult[RecruitmentPhotoResponse](
            authService.encodeJWT(adminUser),
            RecruitmentPhotoResponse.from(recruitment.createPhoto(data, s3Service))).toJson.toString())
        }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.unExistUploadFile).toJson.toString()))
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def deleteAllPhoto(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      recruitmentRepository.fetch(id).map { recruitment =>
        Ok(ResponseResult[RecruitmentResponse](
          authService.encodeJWT(adminUser),
          RecruitmentResponse.from(recruitment.deleteAllPhotos())).toJson.toString())
      }.getOrElse(NotFound)
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def convertFromTmp(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      ConvertRecruitmentFromTmpRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          tmpRecruitmentRepository.fetch(requestData.tmpId).map { tmp =>
            recruitmentRepository.fetch(id).map { to =>
              if (tmp.base.storeId == to.storeId) {
                Ok(ResponseResult[RecruitmentResponse](
                  authService.encodeJWT(adminUser),
                  RecruitmentResponse.from(tmp.convert(to, taskQueueService))).toJson.toString())
              } else {
                Unauthorized(ErrorResponse(ErrorResponse.Message.invalidStoreIdForConvertTmp).toJson.toString())
              }
            }.getOrElse(NotFound)
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}