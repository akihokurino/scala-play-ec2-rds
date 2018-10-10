package repositories

import com.google.inject.ImplementedBy
import infrastructure.BillingDAO
import models.{Billing, PublishedContract}

@ImplementedBy(classOf[BillingRepositoryImpl])
trait BillingRepository {
  def fetchAll(): List[Billing]
  def filterBy(startDate: Option[String],
               endDate: Option[String],
               agencyId: Option[String],
               storeId: Option[String],
               amount: Option[String]): List[Billing]
  def create(contract: PublishedContract): Option[Billing]
  def delete(contract: PublishedContract): Boolean
}

class BillingRepositoryImpl extends BillingRepository {
  def fetchAll(): List[Billing] = BillingDAO.fetchAll()

  def filterBy(startDate: Option[String],
               endDate: Option[String],
               agencyId: Option[String],
               storeId: Option[String],
               amount: Option[String]): List[Billing] = {
    BillingDAO.filterBy(BillingDAO.SearchQuery(startDate, endDate, agencyId, storeId, amount))
  }

  def create(contract: PublishedContract): Option[Billing] = {
    if (BillingDAO.isExist(contract)) {
      None
    } else {
      val insertId = BillingDAO.create(contract)
      BillingDAO.fetchById(insertId)
    }
  }

  def delete(contract: PublishedContract): Boolean = {
    if (BillingDAO.isExist(contract)) {
      BillingDAO.destroy(contract)
    } else {
      false
    }
  }
}

object BillingRepositoryImpl {

}
