package models

import play.api.libs.json.Json

case class DailyEntryLog(date: String, value: String)

object DailyEntryLog {
  implicit def jsonWrites = Json.writes[DailyEntryLog]
  implicit def jsonReads = Json.reads[DailyEntryLog]
}
