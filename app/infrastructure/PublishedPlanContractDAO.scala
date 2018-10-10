package infrastructure

import models.{PublishedContract, PublishedPlanContract}
import requests.{CreatePublishedPlanContractRequest, UpdatePublishedPlanContractRequest}
import scalikejdbc._

case class PublishedPlanContractDAO(id: Int, planId: Int) {

  def to(publishedContractDAO: PublishedContractDAO, publishedPlanDAO: PublishedPlanDAO): PublishedPlanContract =
    PublishedPlanContract(
      id,
      PublishedContract.Type.from(publishedContractDAO.typeId),
      PublishedContract.Status.from(publishedContractDAO.statusId),
      publishedPlanDAO.to(),
      publishedContractDAO.startDate,
      publishedContractDAO.endDate,
      publishedContractDAO.billingAmount,
      publishedContractDAO.createdAt,
      publishedContractDAO.updatedAt)
}

object PublishedPlanContractDAO extends SQLSyntaxSupport[PublishedPlanContractDAO] {
  override val tableName = "published_plan_contracts"

  def apply(r: ResultName[PublishedPlanContractDAO])(rs: WrappedResultSet) =
    new PublishedPlanContractDAO(
      rs.int(r.id),
      rs.int(r.planId)
    )

  private def createSelectQuery(): SelectSQLBuilder[PublishedPlanContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
      .innerJoin(PublishedPlanContractDAO as _publishedPlanContractDAO)
      .on(_publishedPlanContractDAO.id, _publishedContractDAO.id)
      .innerJoin(PublishedPlanDAO as _publishedPlanDAO)
      .on(_publishedPlanContractDAO.planId, _publishedPlanDAO.id)
  }

  private def createSelectWithStoreOverviewQuery(): SelectSQLBuilder[PublishedPlanContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
      .innerJoin(PublishedPlanContractDAO as _publishedPlanContractDAO)
      .on(_publishedPlanContractDAO.id, _publishedContractDAO.id)
      .innerJoin(PublishedPlanDAO as _publishedPlanDAO)
      .on(_publishedPlanContractDAO.planId, _publishedPlanDAO.id)
      .innerJoin(StoreDAO as _storeDAO)
      .on(_publishedContractDAO.storeId, _storeDAO.id)
      .innerJoin(AgencyDAO as _agencyDAO)
      .on(_agencyDAO.id, _storeDAO.agencyId)
      .innerJoin(AdminUserDAO as _adminUserDAO)
      .on(_adminUserDAO.id, _storeDAO.adminUserId)
  }

  private def createContract(rs: WrappedResultSet): PublishedPlanContract = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax

    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val publishedPlanContractDAO = PublishedPlanContractDAO(_publishedPlanContractDAO.resultName)(rs)
    val publishedPlanDAO = PublishedPlanDAO(_publishedPlanDAO.resultName)(rs)

    publishedPlanContractDAO.to(publishedContractDAO, publishedPlanDAO)
  }

  private def createContractWithStoreOverview(rs: WrappedResultSet): PublishedPlanContract.WithStore = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax
    val _publishedPlanDAO = PublishedPlanDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val publishedPlanContractDAO = PublishedPlanContractDAO(_publishedPlanContractDAO.resultName)(rs)
    val publishedPlanDAO = PublishedPlanDAO(_publishedPlanDAO.resultName)(rs)
    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)
    PublishedPlanContract.WithStore(
      publishedPlanContractDAO.to(publishedContractDAO, publishedPlanDAO),
      storeDAO.to(agencyDAO, adminUserDAO))
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[PublishedPlanContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Plan.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfStatus(status: PublishedContract.Status)(implicit s: DBSession = AutoSession): List[PublishedPlanContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Plan.id)
        .and
        .eq(_publishedContractDAO.statusId, status.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfStore(storeId: Int)(implicit s: DBSession = AutoSession): List[PublishedPlanContract] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Plan.id)
        .and
        .eq(_publishedContractDAO.storeId, storeId)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContract }.list().apply()
  }

  def fetchAllOfBetween(storeIds: List[Int], startDate: String, endDate: String)(implicit s: DBSession = AutoSession): List[PublishedPlanContract] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Plan.id)
        .and
        .in(_publishedContractDAO.storeId, storeIds)
        .and
        .ge(_publishedContractDAO.startDate, startDate)
        .and
        .lt(_publishedContractDAO.endDate, endDate)
    }.map { createContract }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[PublishedPlanContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedPlanContractDAO = PublishedPlanContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedPlanContractDAO.id, id)
        .and
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Plan.id)
    }.map { createContractWithStoreOverview }.single().apply()
  }

  def create(data: CreatePublishedPlanContractRequest)(implicit s: DBSession = AutoSession): Int = {
    DB localTx { implicit s =>
      val insertId: Int = PublishedContractDAO.createPlan(data)

      withSQL {
        insert.into(PublishedPlanContractDAO).namedValues(
          column.id -> insertId,
          column.planId -> 1
        )
      }.update().apply()

      insertId
    }
  }

  def edit(id: Int, data: UpdatePublishedPlanContractRequest)(implicit s: DBSession = AutoSession): Boolean =
    PublishedContractDAO.updatePlan(id, data)

  def updateStatus(id: Int, status: PublishedContract.Status)(implicit s: DBSession = AutoSession): Boolean =
    PublishedContractDAO.updateStatus(id, status.id)
}
