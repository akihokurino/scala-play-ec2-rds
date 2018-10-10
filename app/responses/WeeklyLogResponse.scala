package responses

import spray.json._

case class WeeklyLogResponse(totalCount: Option[Int])

object WeeklyLogProtocol extends DefaultJsonProtocol {
  implicit def weeklyLogFormat: RootJsonFormat[WeeklyLogResponse] = jsonFormat1(WeeklyLogResponse.apply)
}

object WeeklyLogResponse {
  def from(value: Option[Int]): WeeklyLogResponse = WeeklyLogResponse(value)
}