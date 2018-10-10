package infrastructure

import models.{Agency, OptionAd, Store}
import org.joda.time.DateTime
import requests.CreateOptionAdRequest
import scalikejdbc._

case class OptionAdDAO(id: Int,
                       storeId: Int,
                       optionId: Int,
                       occupationId: Int,
                       resourceName: String,
                       startDate: String,
                       endDate: String,
                       createdAt: String) {
  def to(publishedOptionDAO: PublishedOptionDAO, occupationDAO: OccupationDAO, store: Store.Overview): OptionAd =
    OptionAd(id, store, publishedOptionDAO.to(), occupationDAO.to(), resourceName, startDate, endDate, createdAt)
}

object OptionAdDAO extends SQLSyntaxSupport[OptionAdDAO] {
  override val tableName = "option_ads"

  def apply(r: ResultName[OptionAdDAO])(rs: WrappedResultSet) =
    new OptionAdDAO(
      rs.int(r.id),
      rs.int(r.storeId),
      rs.int(r.optionId),
      rs.int(r.occupationId),
      rs.string(r.resourceName),
      rs.string(r.startDate),
      rs.string(r.endDate),
      rs.string(r.createdAt)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[OptionAd] = {
    val _optionAdDAO = OptionAdDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(OptionAdDAO as _optionAdDAO)
        .innerJoin(PublishedOptionDAO as _publishedOptionDAO)
        .on(_optionAdDAO.optionId, _publishedOptionDAO.id)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_optionAdDAO.occupationId, _occupationDAO.id)
        .innerJoin(StoreDAO as _storeDAO)
        .on(_optionAdDAO.storeId, _storeDAO.id)
        .innerJoin(AgencyDAO as _agencyDAO)
        .on(_agencyDAO.id, _storeDAO.agencyId)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_adminUserDAO.id, _storeDAO.adminUserId)
        .orderBy(_optionAdDAO.id)
        .asc
    }.map { rs =>
      val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      val store = StoreDAO(_storeDAO.resultName)(rs).to(
        AgencyDAO(_agencyDAO.resultName)(rs),
        AdminUserDAO(_adminUserDAO.resultName)(rs))
      OptionAdDAO(_optionAdDAO.resultName)(rs).to(publishedOptionDAO, occupationDAO, store)
    }.list().apply()
  }

  def fetchAllOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[OptionAd] = {
    val _optionAdDAO = OptionAdDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(OptionAdDAO as _optionAdDAO)
        .innerJoin(PublishedOptionDAO as _publishedOptionDAO)
        .on(_optionAdDAO.optionId, _publishedOptionDAO.id)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_optionAdDAO.occupationId, _occupationDAO.id)
        .innerJoin(StoreDAO as _storeDAO)
        .on(_optionAdDAO.storeId, _storeDAO.id)
        .innerJoin(AgencyDAO as _agencyDAO)
        .on(_agencyDAO.id, _storeDAO.agencyId)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_adminUserDAO.id, _storeDAO.adminUserId)
        .where
        .eq(_agencyDAO.id, agency.id)
        .orderBy(_optionAdDAO.id)
        .asc
    }.map { rs =>
      val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      val store = StoreDAO(_storeDAO.resultName)(rs).to(
        AgencyDAO(_agencyDAO.resultName)(rs),
        AdminUserDAO(_adminUserDAO.resultName)(rs))
      OptionAdDAO(_optionAdDAO.resultName)(rs).to(publishedOptionDAO, occupationDAO, store)
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[OptionAd] = {
    val _optionAdDAO = OptionAdDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _publishedOptionDAO = PublishedOptionDAO.syntax
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    withSQL {
      select.from(OptionAdDAO as _optionAdDAO)
        .innerJoin(PublishedOptionDAO as _publishedOptionDAO)
        .on(_optionAdDAO.optionId, _publishedOptionDAO.id)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_optionAdDAO.occupationId, _occupationDAO.id)
        .innerJoin(StoreDAO as _storeDAO)
        .on(_optionAdDAO.storeId, _storeDAO.id)
        .innerJoin(AgencyDAO as _agencyDAO)
        .on(_agencyDAO.id, _storeDAO.agencyId)
        .innerJoin(AdminUserDAO as _adminUserDAO)
        .on(_adminUserDAO.id, _storeDAO.adminUserId)
        .where
        .eq(_optionAdDAO.id, id)
    }.map { rs =>
      val publishedOptionDAO = PublishedOptionDAO(_publishedOptionDAO.resultName)(rs)
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      val store = StoreDAO(_storeDAO.resultName)(rs).to(
        AgencyDAO(_agencyDAO.resultName)(rs),
        AdminUserDAO(_adminUserDAO.resultName)(rs))
      OptionAdDAO(_optionAdDAO.resultName)(rs).to(publishedOptionDAO, occupationDAO, store)
    }.single().apply()
  }

  def create(data: CreateOptionAdRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(OptionAdDAO).namedValues(
        column.storeId -> data.storeId,
        column.optionId -> data.optionId,
        column.occupationId -> data.occupationId,
        column.resourceName -> "",
        column.startDate -> data.startDate,
        column.endDate -> data.endDate,
        column.createdAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def updateResource(id: Int, resourceName: String)(implicit s: DBSession = AutoSession): Boolean = {

    withSQL {
      update(OptionAdDAO).set(
        column.resourceName -> resourceName
      ).where.eq(OptionAdDAO.column.id, id)
    }.update().apply()

    true
  }

  def destroy(id: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(OptionAdDAO).where.eq(column.id, id)
    }.update().apply()

    true
  }
}