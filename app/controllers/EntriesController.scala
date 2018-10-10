package controllers

import javax.inject.{Inject, Singleton}
import models.AdminUser
import play.api.mvc._
import repositories.EntryRepository
import responses.EntryProtocol._
import responses.WeeklyLogProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.{AuthService, LogService}
import spray.json._

@Singleton
class EntriesController @Inject()(authService: AuthService,
                                  entryRepository: EntryRepository,
                                  logService: LogService) extends Controller {

  def index = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      val entries = adminUser.role match {
        case AdminUser.Role.Master => entryRepository.fetchAll()
        case AdminUser.Role.Agency => adminUser.agency.get.managedEntries
        case AdminUser.Role.Store => adminUser.ownEntries
      }

      Ok(ResponseResults[EntryResponse](
        authService.encodeJWT(adminUser),
        entries.map { EntryResponse.from }).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def weeklySum = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      val totalCount = logService.calcLastWeekEntryNum()
      Ok(ResponseResult[WeeklyLogResponse](
        authService.encodeJWT(adminUser),
        WeeklyLogResponse.from(totalCount)).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}