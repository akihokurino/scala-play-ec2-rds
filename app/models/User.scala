package models

case class User(id: Int,
                name: String,
                birthDate: String,
                gender: User.Gender,
                phoneNumber: String,
                email: String,
                applyMailMagazine: Boolean)

object User {
  sealed abstract class Gender(val id: Int)

  object Gender {
    def from(id: Int): Gender = {
      if (id == 1) {
        Male
      } else if (id == 2) {
        Female
      } else {
        throw new RuntimeException("invalid value")
      }
    }

    case object Male extends Gender(1)
    case object Female extends Gender(2)
  }
}
