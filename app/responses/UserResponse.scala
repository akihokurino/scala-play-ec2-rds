package responses

import models.User
import spray.json._

case class UserResponse(id: Int,
                        name: String,
                        birthDate: String,
                        genderId: Int,
                        phoneNumber: String,
                        email: String,
                        applyMailMagazine: Boolean)

object UserProtocol extends DefaultJsonProtocol {
  implicit def userFormat: RootJsonFormat[UserResponse] = jsonFormat7(UserResponse.apply)
}

object UserResponse {
  def from(user: User): UserResponse =
    UserResponse(
      user.id,
      user.name,
      user.birthDate,
      user.gender.id,
      user.phoneNumber,
      user.email,
      user.applyMailMagazine)
}