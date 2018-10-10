package infrastructure

import models.User
import scalikejdbc._

case class UserDAO(id: Int,
                   name: String,
                   birthDate: String,
                   genderId: Int,
                   phoneNumber: String,
                   email: String,
                   applyMailMagazine: Boolean) {
  def to(): User = User(id, name, birthDate, User.Gender.from(genderId), phoneNumber, email, applyMailMagazine)
}

object UserDAO extends SQLSyntaxSupport[UserDAO] {
  override val tableName = "users"

  def apply(r: ResultName[UserDAO])(rs: WrappedResultSet) =
    new UserDAO(
      rs.int(r.id),
      rs.string(r.name),
      rs.string(r.birthDate),
      rs.int(r.genderId),
      rs.string(r.phoneNumber),
      rs.string(r.email),
      rs.boolean(r.applyMailMagazine)
    )
}
