package models

import play.api.libs.json.Json

case class DailyBillingLog(date: String, value: String)

object DailyBillingLog {
  implicit def jsonWrites = Json.writes[DailyBillingLog]
  implicit def jsonReads = Json.reads[DailyBillingLog]
}
