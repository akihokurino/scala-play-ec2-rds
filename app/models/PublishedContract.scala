package models

trait PublishedContract {
  val id: Int
  val contractType: PublishedContract.Type
  val status: PublishedContract.Status
  val startDate: String
  val endDate: String
  val billingAmount: Int
  val createdAt: String
  val updatedAt: String
  val name: String
}

object PublishedContract {
  sealed abstract class Status(val id: Int) {
    val text: String
  }

  object Status {
    def from(id: Int): Status = {
      id match {
        case 1 => Requesting
        case 2 => Rejected
        case 3 => Approved
        case 4 => Closed
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object Requesting extends Status(1) {
      override val text: String = "申請中"
    }

    case object Rejected extends Status(2) {
      override val text: String = "拒否"
    }

    case object Approved extends Status(3) {
      override val text: String = "承認済"
    }

    case object Closed extends Status(4) {
      override val text: String = "終了"
    }
  }

  sealed abstract class Type(val id: Int) {
    val text: String
  }

  object Type {
    def from(id: Int): Type  = {
      id match {
        case 1 => Plan
        case 2 => Option
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object Plan extends Type(1) {
      override val text: String = "プラン"
    }

    case object Option extends Type(2) {
      override val text: String = "オプション"
    }
  }

  case class WithStore(self: PublishedContract, store: Store.Overview)
}
