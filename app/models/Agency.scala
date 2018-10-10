package models

import java.text.SimpleDateFormat
import java.util.Calendar

import infrastructure._

case class Agency(id: Int, name: String, adminUsers: List[AdminUser]) {

  def managers: List[AdminUser] = AdminUserDAO.fetchAllManagerOfAgency(this)

  def managedEntries: List[Entry] = EntryDAO.fetchAllOfAgency(this)

  def managedOptionAds: List[OptionAd] = OptionAdDAO.fetchAllOfAgency(this)

  def managedOptionContracts: List[PublishedOptionContract.WithStore] = PublishedOptionContractDAO.fetchAllOfAgency(this)

  def managedStores: List[Store.WithAdmin] = StoreDAO.fetchAllOfAgency(this)

  def filterManagedStores(status: Store.Status): List[Store.WithAdmin] = StoreDAO.fetchAllOfAgencyWithFilter(this, status)

  def calcBusinessResults(): Agency.BusinessResult = {
    val storeIds = StoreDAO.fetchIdsOfAgency(this)

    val format = new SimpleDateFormat("y-M-d")
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)

    cal.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfMonth = format.format(cal.getTime)

    cal.add(Calendar.MONTH, 1)
    val startDayOfNextMonth = format.format(cal.getTime)

    val contracts = PublishedPlanContractDAO.fetchAllOfBetween(storeIds, startDayOfMonth, startDayOfNextMonth)
    val totalAmount = contracts.foldLeft(0) { (acc, x) => acc + x.billingAmount }

    Agency.BusinessResult(totalAmount)
  }

  def calcEachBusinessResults(): List[AdminUser.BusinessResult] = managers.map({ it =>
    val storeCount = StoreDAO.countOfAdminUser(it)
    val recruitmentCount = RecruitmentDAO.countOfAdminUser(it)

    val salesAmount = PublishedContractDAO.fetchAllOfAdminUser(it).foldLeft(0) { (sum, contract) => sum + contract.billingAmount }

    AdminUser.BusinessResult(it, storeCount, recruitmentCount, salesAmount)
  })

}

object Agency {
  case class BusinessResult(totalAmount: Int)
}
