package infrastructure

import models._
import org.joda.time.DateTime
import requests.{CreatePublishedOptionContractRequest, CreatePublishedPlanContractRequest, UpdatePublishedOptionContractRequest, UpdatePublishedPlanContractRequest}
import scalikejdbc._

case class PublishedContractDAO(id: Int,
                                storeId: Int,
                                typeId: Int,
                                statusId: Int,
                                startDate: String,
                                endDate: String,
                                billingAmount: Int,
                                createdAt: String,
                                updatedAt: String) {
  def getType: PublishedContract.Type = PublishedContract.Type.from(typeId)
}

object PublishedContractDAO extends SQLSyntaxSupport[PublishedContractDAO] {
  override val tableName = "published_contracts"

  def apply(r: ResultName[PublishedContractDAO])(rs: WrappedResultSet) =
    new PublishedContractDAO(
      rs.int(r.id),
      rs.int(r.storeId),
      rs.int(r.typeId),
      rs.int(r.statusId),
      rs.string(r.startDate),
      rs.string(r.endDate),
      rs.int(r.billingAmount),
      rs.string(r.createdAt),
      rs.string(r.updatedAt)
    )

  private def createSelectQuery(): SelectSQLBuilder[PublishedContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
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
  }

  private def createSelectWithStoreOverviewQuery(): SelectSQLBuilder[PublishedContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
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

  private def createContract(rs: WrappedResultSet): PublishedContract = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax

    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)

    publishedContractDAO.getType match {
      case PublishedContract.Type.Plan =>
        val publishedPlanContractDAO = PublishedPlanContractDAO(_publishedPlanContractDAO.resultName)(rs)
        val publishedPlanDAO = PublishedPlanDAO(_publishedPlanDAO.resultName)(rs)
        publishedPlanContractDAO.to(publishedContractDAO, publishedPlanDAO)
      case PublishedContract.Type.Option =>
        val publishedOptionContractDAO = PublishedOptionContractDAO(_publishedOptionContractDAO.resultName)(rs)
        val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
        publishedOptionContractDAO.to(publishedContractDAO, publishedOptionDAO)
    }
  }

  private def createContractWithStoreOverview(rs: WrappedResultSet): PublishedContract.WithStore = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)

    publishedContractDAO.getType match {
      case PublishedContract.Type.Plan =>
        val publishedPlanContractDAO = PublishedPlanContractDAO(_publishedPlanContractDAO.resultName)(rs)
        val publishedPlanDAO = PublishedPlanDAO(_publishedPlanDAO.resultName)(rs)
        PublishedContract.WithStore(
          publishedPlanContractDAO.to(publishedContractDAO, publishedPlanDAO),
          storeDAO.to(agencyDAO, adminUserDAO))
      case PublishedContract.Type.Option =>
        val publishedOptionContractDAO = PublishedOptionContractDAO(_publishedOptionContractDAO.resultName)(rs)
        val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
        PublishedContract.WithStore(
          publishedOptionContractDAO.to(publishedContractDAO, publishedOptionDAO),
          storeDAO.to(agencyDAO, adminUserDAO))
    }
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[PublishedContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): List[PublishedContract] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.adminUserId, adminUser.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContract }.list().apply()
  }

  def createOption(data: CreatePublishedOptionContractRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(PublishedContractDAO).namedValues(
        column.storeId -> data.storeId,
        column.typeId -> PublishedContract.Type.Option.id,
        column.statusId -> PublishedContract.Status.Requesting.id,
        column.startDate -> data.startDate,
        column.endDate -> data.endDate,
        column.billingAmount -> 0,
        column.createdAt -> currentDate,
        column.updatedAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def createPlan(data: CreatePublishedPlanContractRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(PublishedContractDAO).namedValues(
        column.storeId -> data.storeId,
        column.typeId -> PublishedContract.Type.Plan.id,
        column.statusId -> PublishedContract.Status.Requesting.id,
        column.startDate -> data.startDate,
        column.endDate -> data.endDate,
        column.billingAmount -> PublishedPlan.BILLING_AMOUNT,
        column.createdAt -> currentDate,
        column.updatedAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def updateOption(id: Int, data: UpdatePublishedOptionContractRequest)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(PublishedContractDAO).set(
        column.billingAmount -> data.billingAmount,
        column.startDate -> data.startDate,
        column.endDate -> data.endDate
      ).where.eq(PublishedContractDAO.column.id, id)
    }.update().apply()

    true
  }

  def updatePlan(id: Int, data: UpdatePublishedPlanContractRequest)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(PublishedContractDAO).set(
        column.billingAmount -> data.billingAmount,
        column.startDate -> data.startDate,
        column.endDate -> data.endDate
      ).where.eq(PublishedContractDAO.column.id, id)
    }.update().apply()

    true
  }

  def updateStatus(id: Int, statusId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(PublishedContractDAO).set(column.statusId -> statusId)
        .where
        .eq(PublishedContractDAO.column.id, id)
    }.update().apply()

    true
  }
}