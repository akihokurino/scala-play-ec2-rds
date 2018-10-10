package infrastructure

import models._
import requests.{CreatePublishedOptionContractRequest, UpdatePublishedOptionContractRequest}
import scalikejdbc._

case class PublishedOptionContractDAO(id: Int, optionId: Int) {

  def to(publishedContractDAO: PublishedContractDAO, publishedOptionDAO: PublishedOptionDAO): PublishedOptionContract =
    PublishedOptionContract(
      id,
      PublishedContract.Type.from(publishedContractDAO.typeId),
      PublishedContract.Status.from(publishedContractDAO.statusId),
      publishedOptionDAO.to(),
      publishedContractDAO.startDate,
      publishedContractDAO.endDate,
      publishedContractDAO.billingAmount,
      publishedContractDAO.createdAt,
      publishedContractDAO.updatedAt)
}

object PublishedOptionContractDAO extends SQLSyntaxSupport[PublishedOptionContractDAO] {
  override val tableName = "published_option_contracts"

  def apply(r: ResultName[PublishedOptionContractDAO])(rs: WrappedResultSet) =
    new PublishedOptionContractDAO(
      rs.int(r.id),
      rs.int(r.optionId)
    )

  private def createSelectQuery(): SelectSQLBuilder[PublishedOptionContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
      .innerJoin(PublishedOptionContractDAO as _publishedOptionContractDAO)
      .on(_publishedOptionContractDAO.id, _publishedContractDAO.id)
      .innerJoin(PublishedOptionDAO as _publishedOptionDAO)
      .on(_publishedOptionContractDAO.optionId, _publishedOptionDAO.id)
  }

  private def createSelectWithStoreOverviewQuery(): SelectSQLBuilder[PublishedOptionContractDAO] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(PublishedContractDAO as _publishedContractDAO)
      .innerJoin(PublishedOptionContractDAO as _publishedOptionContractDAO)
      .on(_publishedOptionContractDAO.id, _publishedContractDAO.id)
      .innerJoin(PublishedOptionDAO as _publishedOptionDAO)
      .on(_publishedOptionContractDAO.optionId, _publishedOptionDAO.id)
      .innerJoin(StoreDAO as _storeDAO)
      .on(_publishedContractDAO.storeId, _storeDAO.id)
      .innerJoin(AgencyDAO as _agencyDAO)
      .on(_agencyDAO.id, _storeDAO.agencyId)
      .innerJoin(AdminUserDAO as _adminUserDAO)
      .on(_adminUserDAO.id, _storeDAO.adminUserId)
  }

  private def createContract(rs: WrappedResultSet): PublishedOptionContract = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax

    val publishedContractDAO = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val publishedOptionContractDAO = PublishedOptionContractDAO(_publishedOptionContractDAO.resultName)(rs)
    val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
    publishedOptionContractDAO.to(publishedContractDAO, publishedOptionDAO)
  }

  private def createContractWithStoreOverview(rs: WrappedResultSet): PublishedOptionContract.WithStore = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax


    val publishedContract = PublishedContractDAO(_publishedContractDAO.resultName)(rs)
    val publishedOptionContractDAO = PublishedOptionContractDAO(_publishedOptionContractDAO.resultName)(rs)
    val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)
    PublishedOptionContract.WithStore(publishedOptionContractDAO.to(publishedContract, publishedOptionDAO), storeDAO.to(agencyDAO, adminUserDAO))
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[PublishedOptionContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Option.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfStatus(status: PublishedContract.Status)(implicit s: DBSession = AutoSession): List[PublishedOptionContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Option.id)
        .and
        .eq(_publishedContractDAO.statusId, status.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[PublishedOptionContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _agencyDAO = AgencyDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Option.id)
        .and
        .eq(_agencyDAO.id, agency.id)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContractWithStoreOverview }.list().apply()
  }

  def fetchAllOfStore(storeId: Int)(implicit s: DBSession = AutoSession): List[PublishedOptionContract] = {
    val _publishedContractDAO = PublishedContractDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Option.id)
        .and
        .eq(_publishedContractDAO.storeId, storeId)
        .orderBy(_publishedContractDAO.createdAt)
        .desc
    }.map { createContract }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[PublishedOptionContract.WithStore] = {
    val _publishedContractDAO = PublishedContractDAO.syntax
    val _publishedOptionContractDAO = PublishedOptionContractDAO.syntax

    withSQL {
      createSelectWithStoreOverviewQuery()
        .where
        .eq(_publishedOptionContractDAO.id, id)
        .and
        .eq(_publishedContractDAO.typeId, PublishedContract.Type.Option.id)
    }.map { createContractWithStoreOverview }.single().apply()
  }

  def create(data: CreatePublishedOptionContractRequest)(implicit s: DBSession = AutoSession): Int = {
    DB localTx { implicit s =>
      val insertId: Int = PublishedContractDAO.createOption(data)

      withSQL {
        insert.into(PublishedOptionContractDAO).namedValues(
          column.id -> insertId,
          column.optionId -> data.optionId
        )
      }.update().apply()

      insertId
    }
  }

  def edit(id: Int, data: UpdatePublishedOptionContractRequest)(implicit s: DBSession = AutoSession): Boolean =
    PublishedContractDAO.updateOption(id, data)

  def updateStatus(id: Int, status: PublishedContract.Status)(implicit s: DBSession = AutoSession): Boolean =
    PublishedContractDAO.updateStatus(id, status.id)
}