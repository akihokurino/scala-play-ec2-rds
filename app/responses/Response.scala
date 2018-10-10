package responses

import spray.json._

trait Response {
  val token: String
}

case class ResponseBoolean(token: String, result: Boolean) extends Response
case class ResponseToken(token: String) extends Response
case class ResponseResult[A](token: String, result: A) extends Response
case class ResponseResults[A](token: String, results: List[A]) extends Response

object ResponseJsonProtocol extends DefaultJsonProtocol {
  implicit def responseBoolean: RootJsonFormat[ResponseBoolean] = jsonFormat2(ResponseBoolean.apply)
  implicit def responseTokenFormat: RootJsonFormat[ResponseToken] = jsonFormat1(ResponseToken.apply)
  implicit def responseResultFormat[A: JsonFormat]: RootJsonFormat[ResponseResult[A]] = jsonFormat2(ResponseResult.apply[A])
  implicit def responseResultsFormat[A: JsonFormat]: RootJsonFormat[ResponseResults[A]] = jsonFormat2(ResponseResults.apply[A])
}
