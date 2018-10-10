package infrastructure

import models.{Billing, PublishedContract}
import org.joda.time.DateTime
import scalikejdbc._
import utils.StringUtil

case class BillingDAO(id: Int, publishedContractId: Int, createdAt: String, updatedAt: String) {

  def to(publishedContractDAO: PublishedContractDAO,
         publishedOptionContractDAO: PublishedOptionContractDAO,
         publishedOptionDAO: PublishedOptionDAO,
         storeDAO: StoreDAO,
         agencyDAO: AgencyDAO,
         adminUserDAO: AdminUserDAO): Billing =
    Billing(
      id,
      PublishedContract.WithStore(publishedOptionContractDAO.to(publishedContractDAO, publishedOptionDAO), storeDAO.to(agencyDAO, adminUserDAO)),
      createdAt,
      updatedAt)

  def to(publishedContractDAO: PublishedContractDAO,
         publishedPlanContractDAO: PublishedPlanContractDAO,
         publishedPlanDAO: PublishedPlanDAO,
         storeDAO: StoreDAO,
         agencyDAO: AgencyDAO,
         adminUserDAO: AdminUserDAO): Billing =
    Billing(
      id,
      PublishedContract.WithStore(publishedPlanContractDAO.to(publishedContractDAO, publishedPlanDAO), storeDAO.to(agencyDAO, adminUserDAO)),
      createdAt,
      updatedAt)
}

object BillingDAO extends SQLSyntaxSupport[BillingDAO] {
  override val tableName = "billings"

  def apply(r: ResultName[BillingDAO])(rs: WrappedResultSet) =
    new BillingDAO(
      rs.int(r.id),
      rs.int(r.publishedContractId),
      rs.string(r.createdAt),
      rs.string(r.updatedAt)
    )

  case class SearchQuery(startDate: Option[String], endDate: Option[String], agencyId: Option[String], storeId: Option[String], amount: Option[String]) {

    def buildQuery(query: SelectSQLBuilder[BillingDAO]): ConditionSQLBuilder[BillingDAO] = {
      val _billingDAO = BillingDAO.syntax
      val _publishedContractDAO = PublishedContractDAO.syntax
      val _storeDAO = StoreDAO.syntax
      val _agencyDAO = AgencyDAO.syntax

      val and = (isNeed: Boolean, query: ConditionSQLBuilder[BillingDAO]) => {
        if (isNeed) { query.and } else { query }
      }

      var newQuery = query.where
      var isNeedAnd = false

      if (StringUtil.hasValue(startDate) && StringUtil.hasValue(endDate)) {
        newQuery = and(isNeedAnd, newQuery)
        newQuery = newQuery.ge(_billingDAO.createdAt, startDate.get).and.le(_billingDAO.createdAt, endDate.get)
        isNeedAnd = true
      }

      if (StringUtil.hasValue(agencyId)) {
        newQuery = and(isNeedAnd, newQuery)
        newQuery = newQuery.eq(_agencyDAO.id, agencyId.get)
        isNeedAnd = true
      }

      if (StringUtil.hasValue(storeId)) {
        newQuery = and(isNeedAnd, newQuery)
        newQuery = newQuery.eq(_storeDAO.id, storeId.get)
        isNeedAnd = true
      }

      if (StringUtil.hasValue(amount)) {
        newQuery = and(isNeedAnd, newQuery)
        newQuery = newQuery.eq(_publishedContractDAO.billingAmount, amount.get)
        isNeedAnd = true
      }

      newQuery
    }
  }

  private def createSelect(): SelectSQLBuilder[BillingDAO] = {
    val _billingDAO = BillingDAO.syntax
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(BillingDAO as _billingDAO)
      .innerJoin(PublishedContractDAO as _publishedContractDAO)
      .on(_billingDAO.publishedContractId, _publishedContractDAO.id)
      .leftJoin(PublishedPlanContractDAO as _publishedPlanContractDAO)
      .on(_publishedPlanContractDAO.id, _publishedContractDAO.id)
      .leftJoin(PublishedPlanDAO as _publishedPlanDAO)
      .on(_publishedPlanContractDAO.planId, _publishedPlanDAO.id)
      .leftJoin(PublishedOptionContractDAO as _publishedOptionContractDAO)
      .on(_publishedOptionContractDAO.id, _publishedContractDAO.id)
      .leftJoin(PublishedOptionDAO as _publishedOptionDAO)
      .on(_publishedOptionContractDAO.optionId, _publishedOptionDAO.id)
      .innerJoin(StoreDAO as _storeDAO)
      .on(_publishedContractDAO.storeId, _storeDAO.id)
      .innerJoin(AgencyDAO as _agencyDAO)
      .on(_agencyDAO.id, _storeDAO.agencyId)
      .innerJoin(AdminUserDAO as _adminUserDAO)
      .on(_adminUserDAO.id, _storeDAO.adminUserId)
  }

  private def createBilling(rs: WrappedResultSet): Billing = {
    val _billingDAO = BillingDAO.syntax
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    val billingDAO = BillingDAO(_billingDAO.resultName)(rs)
    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)

    publishedContractDAO.getType match {
      case PublishedContract.Type.Plan =>
        val publishedPlanContractDAO = PublishedPlanContractDAO(_publishedPlanContractDAO.resultName)(rs)
        val publishedPlanDAO = PublishedPlanDAO(_publishedPlanDAO.resultName)(rs)
        billingDAO.to(publishedContractDAO, publishedPlanContractDAO, publishedPlanDAO, storeDAO, agencyDAO, adminUserDAO)
      case PublishedContract.Type.Option =>
        val publishedOptionContractDAO = PublishedOptionContractDAO(_publishedOptionContractDAO.resultName)(rs)
        val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
        billingDAO.to(publishedContractDAO, publishedOptionContractDAO, publishedOptionDAO, storeDAO, agencyDAO, adminUserDAO)
    }
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[Billing] = {
    val _billingDAO = BillingDAO.syntax

    withSQL {
      createSelect().orderBy(_billingDAO.id).desc
    }.map { createBilling }.list().apply()
  }

  def filterBy(query: SearchQuery)(implicit s: DBSession = AutoSession): List[Billing] = {
    val _billingDAO = BillingDAO.syntax

    withSQL {
      query.buildQuery(createSelect()).orderBy(_billingDAO.id).desc
    }.map { createBilling }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Billing] = {
    val _billingDAO = BillingDAO.syntax

    withSQL {
      createSelect().where.eq(_billingDAO.id, id)
    }.map { createBilling }.single().apply()
  }

  def create(contract: PublishedContract)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(BillingDAO).namedValues(
        column.publishedContractId -> contract.id,
        column.createdAt -> currentDate,
        column.updatedAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def destroy(contract: PublishedContract)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(BillingDAO).where.eq(column.publishedContractId, contract.id)
    }.update().apply()

    true
  }

  def isExist(contract: PublishedContract)(implicit s: DBSession = AutoSession): Boolean = {
    val _billingDAO = BillingDAO.syntax

    val result = withSQL {
      select(_billingDAO.id).from(BillingDAO as _billingDAO)
        .where
        .eq(_billingDAO.publishedContractId, contract.id)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }
}