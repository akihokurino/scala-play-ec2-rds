package services

import com.google.inject.ImplementedBy
import io.really.jwt._
import models.AdminUser
import play.api.data.validation.{Constraint, Constraints}
import play.api.libs.json._
import play.api.mvc.Headers

@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
  def encodeJWT(user: AdminUser): String
  def authenticate(headers: Headers): Option[AdminUser]
  def authenticateOnly(headers: Headers, roles: Set[AdminUser.Role]): Option[AdminUser]
}

class AuthServiceImpl extends AuthService {
  def authenticate(headers: Headers): Option[AdminUser] = headers.get("authorization").flatMap { decodeJWT }

  def authenticateOnly(headers: Headers, roles: Set[AdminUser.Role]): Option[AdminUser] =
    authenticate(headers).flatMap { adminUser =>
      if (roles.contains(adminUser.role)) {
        Some(adminUser)
      } else {
        None
      }
    }


  def encodeJWT(user: AdminUser): String = {
    val payload = user.storeId match {
      case Some(id) => Json.obj(
        "id" -> user.id,
        "username" -> user.username,
        "email" -> user.email,
        "role_id" -> user.role.id,
        "store_id" -> id)
      case None => Json.obj(
        "id" -> user.id,
        "username" -> user.username,
        "email" -> user.email,
        "role_id" -> user.role.id)
    }

    JWT.encode(AuthServiceImpl.SECRET_KEY, payload, Json.obj(), Some(Algorithm.HS256))
  }

  private def decodeJWT(token: String): Option[AdminUser] = {
    JWT.decode(token, Some(AuthServiceImpl.SECRET_KEY)) match {
      case jwt: JWTResult.JWT =>
        Some(AdminUser(
          jwt.payload.value("id").toString().toInt,
          jwt.payload.value("username").toString(),
          jwt.payload.value("email").toString(),
          AdminUser.Role.from(jwt.payload.value("role_id").toString().toInt),
          if (jwt.payload.keys.contains("store_id")) {
            Some(jwt.payload.value("store_id").toString().toInt)
          } else {
            None
          }
        ))
      case _ => None
    }
  }
}

object AuthServiceImpl {
  private val SECRET_KEY: String = "secret-key"

  // TODO: Emailの正規表現
  val emailPattern: Constraint[String] = Constraints.pattern(".*".r)
}
