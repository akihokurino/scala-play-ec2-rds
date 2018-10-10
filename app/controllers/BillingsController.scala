package controllers

import javax.inject.{Inject, Singleton}
import models.AdminUser
import play.api.mvc._
import repositories.BillingRepository
import responses.BillingProtocol._
import responses.WeeklyLogProtocol._
import responses.ResponseJsonProtocol._
import responses._
import services.{AuthService, LogService}
import spray.json._
import utils.StringUtil

@Singleton
class BillingsController @Inject()(authService: AuthService,
                                   billingRepository: BillingRepository,
                                   logService: LogService) extends Controller {

  def index(startDate: Option[String], endDate: Option[String], agencyId: Option[String], storeId: Option[String], amount: Option[String]) = Action { implicit request =>
    authService.authenticate(request.headers).map { adminUser =>
      Ok(ResponseResults[BillingResponse](
        authService.encodeJWT(adminUser),
        if ((StringUtil.hasValue(startDate) && StringUtil.hasValue(endDate)) ||
          StringUtil.hasValue(agencyId) ||
          StringUtil.hasValue(storeId) ||
          StringUtil.hasValue(amount)) {
          billingRepository.filterBy(startDate, endDate, agencyId, storeId, amount).map({ BillingResponse.from })
        } else {
          billingRepository.fetchAll().map({ BillingResponse.from })
        }).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }

  def weeklySum = Action { implicit request =>
    authService.authenticateOnly(request.headers, Set(AdminUser.Role.Master)).map { adminUser =>
      val totalCount = logService.calcLastWeekBillingAmount()
      Ok(ResponseResult[WeeklyLogResponse](
        authService.encodeJWT(adminUser),
        WeeklyLogResponse.from(totalCount)).toJson.toString())
    }.getOrElse(Unauthorized(ErrorResponse(ErrorResponse.Message.invalidAuth).toJson.toString()))
  }
}
