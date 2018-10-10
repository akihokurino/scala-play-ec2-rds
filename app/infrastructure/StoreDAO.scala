package infrastructure

import models._
import org.joda.time.DateTime
import requests.{CreateStoreRequest, UpdateStoreRequest}
import scalikejdbc._
import sqls.{count, distinct}

case class StoreDAO(id: Int,
                    agencyId: Int,
                    adminUserId: Int,
                    businessConditionId: Int,
                    statusId: Int,
                    prefectureId: Int,
                    areaId: Option[Int],
                    requestedDate: String,
                    name: String,
                    nameKana: String,
                    postalCode: String,
                    address: String,
                    buildingName: String,
                    phoneNumber: String,
                    restaurantPermissionNumber: String,
                    customsPermissionNumber: String,
                    managerName: String,
                    managerNameKana: String,
                    managerEmail: String,
                    managerSubEmail: String,
                    createdAt: String,
                    updatedAt: String) {

  private def to(recruitment: Option[Recruitment],
                 tmpRecruitment: Option[TmpRecruitment],
                 businessConditionDAO: BusinessConditionDAO,
                 prefectureDAO: PrefectureDAO,
                 areaDAO: Option[AreaDAO]): Store = {
    val nearestStations = NearestStationDAO.fetchAllOfStore(id)

    val info = Store.Info(
      name,
      nameKana,
      postalCode,
      prefectureDAO.to(),
      areaDAO.map { it => it.to() },
      address,
      nearestStations,
      buildingName,
      phoneNumber,
      restaurantPermissionNumber,
      customsPermissionNumber)

    val manager = Store.Manager(managerName, managerNameKana, managerEmail, managerSubEmail)

    val publishedOptionContracts = PublishedOptionContractDAO.fetchAllOfStore(id)
    val publishedPlanContracts = PublishedPlanContractDAO.fetchAllOfStore(id)

    Store(
      id,
      businessConditionDAO.to(),
      Store.Status.from(statusId),
      info,
      manager,
      recruitment,
      tmpRecruitment,
      requestedDate,
      createdAt,
      updatedAt,
      publishedOptionContracts,
      publishedPlanContracts)
  }

  def to(recruitmentDao: RecruitmentDAO,
         occupationDAO: OccupationDAO,
         businessConditionDAO: BusinessConditionDAO,
         prefectureDAO: PrefectureDAO,
         areaDAO: Option[AreaDAO]): Store = {
    to(Some(recruitmentDao.to(occupationDAO)), None, businessConditionDAO, prefectureDAO, areaDAO)
  }

  def to(recruitmentDao: RecruitmentDAO,
         occupationDAO: OccupationDAO,
         tmpRecruitmentDao: TmpRecruitmentDAO,
         businessConditionDAO: BusinessConditionDAO,
         prefectureDAO: PrefectureDAO,
         areaDAO: Option[AreaDAO]): Store = {
    to(
      Some(recruitmentDao.to(occupationDAO)),
      Some(tmpRecruitmentDao.to()),
      businessConditionDAO,
      prefectureDAO,
      areaDAO)
  }

  def to(businessConditionDAO: BusinessConditionDAO,
         prefectureDAO: PrefectureDAO,
         areaDAO: Option[AreaDAO]): Store = {
    to(None, None, businessConditionDAO, prefectureDAO, areaDAO)
  }

  def to(agencyDAO: AgencyDAO, adminUserDAO: AdminUserDAO): Store.Overview = {
    Store.Overview(id, name, agencyDAO.to(), adminUserDAO.to())
  }

  val isRequesting: Boolean = Store.Status.from(statusId) == Store.Status.Requesting
  val isRejected: Boolean = Store.Status.from(statusId) == Store.Status.Rejected
}

object StoreDAO extends SQLSyntaxSupport[StoreDAO] {
  override val tableName = "stores"

  def apply(r: ResultName[StoreDAO])(rs: WrappedResultSet) =
    new StoreDAO(
      rs.int(r.id),
      rs.int(r.agencyId),
      rs.int(r.adminUserId),
      rs.int(r.businessConditionId),
      rs.int(r.statusId),
      rs.int(r.prefectureId),
      rs.intOpt(r.areaId),
      rs.string(r.requestedDate),
      rs.string(r.name),
      rs.string(r.nameKana),
      rs.string(r.postalCode),
      rs.string(r.address),
      rs.string(r.buildingName),
      rs.string(r.phoneNumber),
      rs.string(r.restaurantPermissionNumber),
      rs.string(r.customsPermissionNumber),
      rs.string(r.managerName),
      rs.string(r.managerNameKana),
      rs.string(r.managerEmail),
      rs.string(r.managerSubEmail),
      rs.string(r.createdAt),
      rs.string(r.updatedAt)
    )

  private def createSelectQuery(): SelectSQLBuilder[StoreDAO] = {
    val _storeDAO = StoreDAO.syntax
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _tmpRecruitmentDAO = TmpRecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _businessConditionDAO = BusinessConditionDAO.syntax
    val _prefectureDAO = PrefectureDAO.syntax
    val _areaDAO = AreaDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(StoreDAO as _storeDAO)
      .leftJoin(RecruitmentDAO as _recruitmentDAO)
      .on(_storeDAO.id, _recruitmentDAO.storeId)
      .leftJoin(OccupationDAO as _occupationDAO)
      .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
      .leftJoin(TmpRecruitmentDAO as _tmpRecruitmentDAO)
      .on(_storeDAO.id, _tmpRecruitmentDAO.storeId)
      .innerJoin(BusinessConditionDAO as _businessConditionDAO)
      .on(_businessConditionDAO.id, _storeDAO.businessConditionId)
      .innerJoin(PrefectureDAO as _prefectureDAO)
      .on(_prefectureDAO.id, _storeDAO.prefectureId)
      .leftJoin(AreaDAO as _areaDAO)
      .on(_areaDAO.id, _storeDAO.areaId)
      .innerJoin(AgencyDAO as _agencyDAO)
      .on(_agencyDAO.id, _storeDAO.agencyId)
      .innerJoin(AdminUserDAO as _adminUserDAO)
      .on(_adminUserDAO.id, _storeDAO.adminUserId)
  }

  private def createSelectOverviewQuery(): SelectSQLBuilder[StoreDAO] = {
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    select.from(StoreDAO as _storeDAO)
      .innerJoin(AgencyDAO as _agencyDAO)
      .on(_agencyDAO.id, _storeDAO.agencyId)
      .innerJoin(AdminUserDAO as _adminUserDAO)
      .on(_adminUserDAO.id, _storeDAO.adminUserId)
  }

  private def createStore(rs: WrappedResultSet): Store.WithAdmin = {
    val _storeDAO = StoreDAO.syntax
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _tmpRecruitmentDAO = TmpRecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax
    val _businessConditionDAO = BusinessConditionDAO.syntax
    val _prefectureDAO = PrefectureDAO.syntax
    val _areaDAO = AreaDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val businessConditionDAO = BusinessConditionDAO(_businessConditionDAO.resultName)(rs)
    val prefectureDAO = PrefectureDAO(_prefectureDAO.resultName)(rs)

    val areaDAO: Option[AreaDAO] = if (Prefecture.hasArea(prefectureDAO.id) && storeDAO.areaId.isDefined) {
      Some(AreaDAO(_areaDAO.resultName)(rs))
    } else {
      None
    }

    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)

    if (storeDAO.isRequesting || storeDAO.isRejected) {
      Store.WithAdmin(storeDAO.to(businessConditionDAO, prefectureDAO, areaDAO), agencyDAO.to(), adminUserDAO.to())
    } else if (isExistRecruitment(storeDAO.id)) {
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      val recruitmentDAO = RecruitmentDAO(_recruitmentDAO.resultName)(rs)

      if (isExistTmpRecruitment(storeDAO.id)) {
        val tmpRecruitmentDAO = TmpRecruitmentDAO(_tmpRecruitmentDAO.resultName)(rs)

        Store.WithAdmin(
          storeDAO.to(recruitmentDAO, occupationDAO, tmpRecruitmentDAO, businessConditionDAO, prefectureDAO, areaDAO),
          agencyDAO.to(),
          adminUserDAO.to())
      } else {
        Store.WithAdmin(
          storeDAO.to(recruitmentDAO, occupationDAO, businessConditionDAO, prefectureDAO, areaDAO),
          agencyDAO.to(),
          adminUserDAO.to())
      }
    } else {
      Store.WithAdmin(storeDAO.to(businessConditionDAO, prefectureDAO, areaDAO), agencyDAO.to(), adminUserDAO.to())
    }
  }

  private def createStoreOverview(rs: WrappedResultSet): Store.Overview = {
    val _storeDAO = StoreDAO.syntax
    val _agencyDAO = AgencyDAO.syntax
    val _adminUserDAO = AdminUserDAO.syntax

    val storeDAO = StoreDAO(_storeDAO.resultName)(rs)
    val agencyDAO = AgencyDAO(_agencyDAO.resultName)(rs)
    val adminUserDAO = AdminUserDAO(_adminUserDAO.resultName)(rs)
    storeDAO.to(agencyDAO, adminUserDAO)
  }

  def fetchAll()(implicit s: DBSession = AutoSession): List[Store.WithAdmin] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
        createSelectQuery()
          .orderBy(_storeDAO.updatedAt)
          .desc
    }.map { createStore }.list().apply()
  }

  def fetchAllOfStatus(status: Store.Status)(implicit s: DBSession = AutoSession): List[Store.WithAdmin] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.statusId, status.id)
        .orderBy(_storeDAO.updatedAt)
        .desc
    }.map { createStore }.list().apply()
  }

  def fetchAllOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[Store.WithAdmin] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.agencyId, agency.id)
        .orderBy(_storeDAO.updatedAt)
        .desc
    }.map { createStore }.list().apply()
  }

  def fetchAllOfAgencyWithFilter(agency: Agency, status: Store.Status)(implicit s: DBSession = AutoSession): List[Store.WithAdmin] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.agencyId, agency.id)
        .and
        .eq(_storeDAO.statusId, status.id)
        .orderBy(_storeDAO.updatedAt)
        .desc
    }.map { createStore }.list().apply()
  }

  def fetchAllOfStoreIds(storeIds: List[Int])(implicit s: DBSession = AutoSession): List[Store.Overview] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectOverviewQuery()
        .where
        .in(_storeDAO.id, storeIds)
        .orderBy(_storeDAO.updatedAt)
        .desc
    }.map { createStoreOverview }.list().apply()
  }

  def fetchAllOfAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): List[Store.Overview] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectOverviewQuery()
        .where
        .eq(_storeDAO.adminUserId, adminUser.id)
        .orderBy(_storeDAO.updatedAt)
        .desc
    }.map { createStoreOverview }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Store.WithAdmin] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      createSelectQuery()
        .where
        .eq(_storeDAO.id, id)
    }.map { createStore }.single().apply()
  }

  def countOfAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): Int = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      select(count(distinct(_storeDAO.id))).from(StoreDAO as _storeDAO)
        .where
        .eq(_storeDAO.adminUserId, adminUser.id)
    }.map(_.int(1)).single.apply().get
  }

  def fetchIdsOfAgency(agency: Agency)(implicit s: DBSession = AutoSession): List[Int] = {
    val _storeDAO = StoreDAO.syntax

    withSQL {
      select(_storeDAO.result.id).from(StoreDAO as _storeDAO)
        .where
        .eq(_storeDAO.agencyId, agency.id)
    }.map(rs => rs.int(_storeDAO.resultName.id)).list.apply()
  }

  def create(data: CreateStoreRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    withSQL {
      insert.into(StoreDAO).namedValues(
        column.agencyId -> data.agencyId,
        column.adminUserId -> data.adminUserId,
        column.businessConditionId -> data.businessConditionId,
        column.statusId -> Store.Status.Requesting.id,
        column.prefectureId -> data.prefectureId,
        column.areaId -> data.areaId,
        column.requestedDate -> data.requestedDate,
        column.name -> data.name,
        column.nameKana -> data.nameKana,
        column.postalCode -> data.postalCode,
        column.address -> data.address,
        column.buildingName -> data.buildingName,
        column.phoneNumber -> data.phoneNumber,
        column.restaurantPermissionNumber -> data.restaurantPermissionNumber,
        column.customsPermissionNumber -> data.customsPermissionNumber,
        column.managerName -> data.managerName,
        column.managerNameKana -> data.managerNameKana,
        column.managerEmail -> data.managerEmail,
        column.managerSubEmail -> data.managerSubEmail,
        column.createdAt -> currentDate,
        column.updatedAt -> currentDate
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def edit(id: Int, data: UpdateStoreRequest)(implicit s: DBSession = AutoSession): Boolean = {
    val currentDate = DateTime.now

    withSQL {
      update(StoreDAO).set(
        column.businessConditionId -> data.businessConditionId,
        column.statusId -> Store.Status.Requesting.id,
        column.prefectureId -> data.prefectureId,
        column.areaId -> data.areaId,
        column.name -> data.name,
        column.nameKana -> data.nameKana,
        column.postalCode -> data.postalCode,
        column.address -> data.address,
        column.buildingName -> data.buildingName,
        column.phoneNumber -> data.phoneNumber,
        column.restaurantPermissionNumber -> data.restaurantPermissionNumber,
        column.customsPermissionNumber -> data.customsPermissionNumber,
        column.managerName -> data.managerName,
        column.managerNameKana -> data.managerNameKana,
        column.managerEmail -> data.managerEmail,
        column.managerSubEmail -> data.managerSubEmail,
        column.updatedAt -> currentDate
      ).where.eq(StoreDAO.column.id, id)
    }.update().apply()

    true
  }

  /**
    * leftJoinでnullかどうかの分岐ができるようになるまでの暫定メソッド
    */
  def isExistRecruitment(storeId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    val _recruitmentDAO = RecruitmentDAO.syntax

    val result = withSQL {
      select(_recruitmentDAO.id).from(RecruitmentDAO as _recruitmentDAO)
        .where
        .eq(_recruitmentDAO.storeId, storeId)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }

  def isExistTmpRecruitment(storeId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    val _recruitmentDAO = TmpRecruitmentDAO.syntax

    val result = withSQL {
      select(_recruitmentDAO.id).from(TmpRecruitmentDAO as _recruitmentDAO)
        .where
        .eq(_recruitmentDAO.storeId, storeId)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }

  def updateStatus(id: Int, status: Store.Status)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(StoreDAO).set(column.statusId -> status.id)
        .where
        .eq(StoreDAO.column.id, id)
    }.update().apply()

    true
  }

  def isExistThisName(name: String)(implicit s: DBSession = AutoSession): Boolean = {
    val _storeDAO = StoreDAO.syntax

    val result = withSQL {
      select(_storeDAO.id).from(StoreDAO as _storeDAO)
        .where
        .eq(_storeDAO.name, name)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(_) => true
      case None => false
    }
  }

  def isExistThisNameExclude(name: String, excludeId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    val _storeDAO = StoreDAO.syntax

    val result = withSQL {
      select(_storeDAO.id).from(StoreDAO as _storeDAO)
        .where
        .eq(_storeDAO.name, name)
    }.map(_.int(1)).single.apply()

    result match {
      case Some(id) => id != excludeId
      case None => false
    }
  }
}