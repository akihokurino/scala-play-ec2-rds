package controllers

import javax.inject._

import models.AdminUser
import play.api.mvc._
import repositories.AdminUserRepository
import requests._
import responses.AdminUserProtocol._
import responses.BusinessResultProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.AuthService
import spray.json._

@Singleton
class AdminUsersController @Inject()(authService: AuthService,
                                     adminUserRepository: AdminUserRepository) extends Controller {

  def index() = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      val query = request.queryString.map { case (k,v) => k -> v.mkString }
      val users = if (query.get("store_id").isDefined) {
        adminUserRepository.fetchAllStoreOwnerOf(query("store_id").toInt)
      } else if (query.get("filter").isDefined && query("filter") == "store") {
        adminUserRepository.fetchAllStoreOwner()
      } else {
        adminUser.agency match {
          case Some(agency) => agency.managers
          case None => List.empty
        }
      }

      Ok(ResponseResults[AdminUserResponse](
        authService.encodeJWT(adminUser),
        users.map({ AdminUserResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def businessResults = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      val results = adminUser.agency match {
        case Some(agency) => agency.calcEachBusinessResults()
        case None => List.empty
      }

      Ok(ResponseResults[BusinessResultResponse](
        authService.encodeJWT(adminUser),
        results.map({ BusinessResultResponse.from })).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def me = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResult[AdminUserResponse](
        authService.encodeJWT(adminUser),
        AdminUserResponse.from(adminUser)).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def signIn = Action { implicit request =>
    SignInRequest.form.bindFromRequest.fold(
      e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
      requestData => {
        adminUserRepository.authenticate(requestData).map { adminUser =>
          Ok(ResponseResult[AdminUserResponse](
            authService.encodeJWT(adminUser),
            AdminUserResponse.from(adminUser)).toJson.toString())
        }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidSignIn).toJson.toString()))
      }
    )
  }

  def createOfAgency = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateAgencyAdminUserRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          val _ = adminUserRepository.create(requestData)
          Ok(ResponseToken(authService.encodeJWT(adminUser)).toJson.toString())
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def createOfStore = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      CreateStoreAdminUserRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          val _ = adminUserRepository.create(requestData)
          Ok(ResponseToken(authService.encodeJWT(adminUser)).toJson.toString())
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def connectToStore(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      ConnectAdminUserToStoreRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          adminUserRepository.fetch(id).map { user =>
            user.connectToStore(requestData.storeId)
            Ok(ResponseToken(authService.encodeJWT(adminUser)).toJson.toString())
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def update(id: Int) = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Agency)).map { adminUser =>
      UpdateAdminUserRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          adminUserRepository.fetch(id).map { updateUser =>
            if (adminUser.checkUpdateAuthority(updateUser.id)) {
              Ok(ResponseResult[AdminUserResponse](
                authService.encodeJWT(adminUser),
                AdminUserResponse.from(updateUser.update(requestData))).toJson.toString())
            } else {
              Unauthorized(ErrorResponse(ErrorResponse.Message.notAuthorityUpdateAdminUser).toJson.toString())
            }
          }.getOrElse(NotFound)
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def updatePassword(id: Int) = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      UpdateAdminUserPasswordRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          adminUserRepository.authenticate(SignInRequest(requestData.email, requestData.oldPassword)).flatMap({ updateUser =>
            if (updateUser.id == id) {
              Some(updateUser)
            } else {
              None
            }
          }).map { updateUser =>
            Ok(ResponseResult[AdminUserResponse](
              authService.encodeJWT(adminUser),
              AdminUserResponse.from(updateUser.update(requestData))).toJson.toString())
          }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidSignIn).toJson.toString()))
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def checkEmailDuplication = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      val query = request.queryString.map { case (k,v) => k -> v.mkString }
      query.get("email").map { email =>
        Ok(ResponseBoolean(
          authService.encodeJWT(adminUser),
          adminUserRepository.isExist(email)).toJson.toString())
      }.getOrElse(BadRequest(ErrorResponse(ErrorResponse.Message.duplicateEmail).toJson.toString()))
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def storeSignIn = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Store)).map { adminUser =>
      StoreSignInRequest.form.bindFromRequest.fold(
        e => BadRequest(FormErrorResponse(e.errors).toJson.toString()),
        requestData => {
          adminUser.authenticateStore(requestData.storeId).map { newAdminUser =>
            Ok(ResponseResult[AdminUserResponse](
              authService.encodeJWT(newAdminUser),
              AdminUserResponse.from(newAdminUser)).toJson.toString())
          }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidStoreSignIn).toJson.toString()))
        }
      )
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
