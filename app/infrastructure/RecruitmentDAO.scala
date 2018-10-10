package infrastructure

import infrastructure.intermediate.{RecruitmentOccupationDAO, RecruitmentSpecificTagDAO, TmpRecruitmentOccupationDAO, TmpRecruitmentSpecificTagDAO}
import models._
import org.joda.time.DateTime
import requests.{CreateRecruitmentRequest, UpdateRecruitmentRequest}
import scalikejdbc._
import sqls.{count, distinct}

case class RecruitmentDAO(id: Int,
                          storeId: Int,
                          statusId: Int,
                          displayOccupationId: Int,
                          displayPaymentTypeId: Int,
                          displayPaymentFrom: Int,
                          displayPaymentTo: Option[Int],
                          title: String,
                          pr: String,
                          workInfo: String,
                          paymentInfo: String,
                          workingHoursInfo: String,
                          holidayInfo: String,
                          requirementInfo: String,
                          treatmentInfo: String,
                          entryMethodInfo: String,
                          lineUrl: String,
                          createdAt: String,
                          updatedAt: String) {

  def to(displayOccupationDAO: OccupationDAO): Recruitment = {
    val info = Recruitment.Info(
      title,
      pr,
      workInfo,
      paymentInfo,
      workingHoursInfo,
      holidayInfo,
      requirementInfo,
      treatmentInfo,
      entryMethodInfo,
      lineUrl)

    val display = Recruitment.Display(
      displayOccupationDAO.to(),
      Recruitment.PaymentType.from(displayPaymentTypeId),
      displayPaymentFrom,
      displayPaymentTo
    )

    val occupations = OccupationDAO.fetchAllOfRecruitment(id)
    val photos = RecruitmentPhotoDAO.fetchAllOfRecruitment(id)
    val tags = SpecificTagDAO.fetchAllOfRecruitment(id)

    Recruitment(
      id,
      storeId,
      Recruitment.Status.from(statusId),
      display,
      info,
      createdAt,
      updatedAt,
      occupations,
      photos,
      tags)
  }
}

object RecruitmentDAO extends SQLSyntaxSupport[RecruitmentDAO] {
  override val tableName = "recruitments"

  def apply(r: ResultName[RecruitmentDAO])(rs: WrappedResultSet) =
    new RecruitmentDAO(
      rs.int(r.id),
      rs.int(r.storeId),
      rs.int(r.statusId),
      rs.int(r.displayOccupationId),
      rs.int(r.displayPaymentTypeId),
      rs.int(r.displayPaymentFrom),
      rs.intOpt(r.displayPaymentTo),
      rs.string(r.title),
      rs.string(r.pr),
      rs.string(r.workInfo),
      rs.string(r.paymentInfo),
      rs.string(r.workingHoursInfo),
      rs.string(r.holidayInfo),
      rs.string(r.requirementInfo),
      rs.string(r.treatmentInfo),
      rs.string(r.entryMethodInfo),
      rs.string(r.lineUrl),
      rs.string(r.createdAt),
      rs.string(r.updatedAt)
    )

  def fetchAllOfStatus(status: Recruitment.Status)(implicit s: DBSession = AutoSession): List[Recruitment] = {
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(RecruitmentDAO as _recruitmentDAO)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
        .where
        .eq(_recruitmentDAO.statusId, status.id)
    }.map { rs =>
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      RecruitmentDAO(_recruitmentDAO.resultName)(rs).to(occupationDAO)
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Recruitment] = {
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(RecruitmentDAO as _recruitmentDAO)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
        .where
        .eq(_recruitmentDAO.id, id)
    }.map { rs =>
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      RecruitmentDAO(_recruitmentDAO.resultName)(rs).to(occupationDAO)
    }.single().apply()
  }

  def fetchByStoreId(storeId: Int)(implicit s: DBSession = AutoSession): Option[Recruitment] = {
    val _recruitmentDAO = RecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(RecruitmentDAO as _recruitmentDAO)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
        .where
        .eq(_recruitmentDAO.storeId, storeId)
    }.map { rs =>
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      RecruitmentDAO(_recruitmentDAO.resultName)(rs).to(occupationDAO)
    }.single().apply()
  }

  def countOfAdminUser(adminUser: AdminUser)(implicit s: DBSession = AutoSession): Int = {
    val _storeDAO = StoreDAO.syntax
    val _recruitmentDAO = RecruitmentDAO.syntax

    withSQL {
      select(count(distinct(_recruitmentDAO.id))).from(StoreDAO as _storeDAO)
        .innerJoin(RecruitmentDAO as _recruitmentDAO)
        .on(_storeDAO.id, _recruitmentDAO.storeId)
        .where
        .eq(_storeDAO.adminUserId, adminUser.id)
    }.map(_.int(1)).single.apply().get
  }

  def create(data: CreateRecruitmentRequest)(implicit s: DBSession = AutoSession): Int = {
    val currentDate = DateTime.now

    DB localTx { implicit s =>
      val insertId: Int = withSQL {
        insert.into(RecruitmentDAO).namedValues(
          column.storeId -> data.storeId,
          column.statusId -> Recruitment.Status.Requesting.id,
          column.displayOccupationId -> data.displayOccupationId,
          column.displayPaymentTypeId -> data.displayPaymentTypeId,
          column.displayPaymentFrom -> data.displayPaymentFrom,
          column.displayPaymentTo -> data.displayPaymentTo,
          column.title -> data.title,
          column.pr -> data.pr,
          column.workInfo -> data.workInfo,
          column.paymentInfo -> data.paymentInfo,
          column.workingHoursInfo -> data.workingHoursInfo,
          column.holidayInfo -> data.holidayInfo,
          column.requirementInfo -> data.requirementInfo,
          column.treatmentInfo -> data.treatmentInfo,
          column.entryMethodInfo -> data.entryMethodInfo,
          column.lineUrl -> data.lineUrl,
          column.createdAt -> currentDate,
          column.updatedAt -> currentDate
        )
      }.updateAndReturnGeneratedKey().apply().toInt

      data.occupationIds.foreach({ it => RecruitmentOccupationDAO.create(insertId, it) })
      data.specificTagIds.foreach({ it => RecruitmentSpecificTagDAO.create(insertId, it) })

      insertId
    }
  }

  def edit(id: Int, data: UpdateRecruitmentRequest)(implicit s: DBSession = AutoSession): Boolean = {
    val currentDate = DateTime.now

    DB localTx { implicit s =>
      withSQL {
        update(RecruitmentDAO).set(
          column.statusId -> Recruitment.Status.Requesting.id,
          column.displayOccupationId -> data.displayOccupationId,
          column.displayPaymentTypeId -> data.displayPaymentTypeId,
          column.displayPaymentFrom -> data.displayPaymentFrom,
          column.displayPaymentTo -> data.displayPaymentTo,
          column.title -> data.title,
          column.pr -> data.pr,
          column.workInfo -> data.workInfo,
          column.paymentInfo -> data.paymentInfo,
          column.workingHoursInfo -> data.workingHoursInfo,
          column.holidayInfo -> data.holidayInfo,
          column.requirementInfo -> data.requirementInfo,
          column.treatmentInfo -> data.treatmentInfo,
          column.entryMethodInfo -> data.entryMethodInfo,
          column.lineUrl -> data.lineUrl,
          column.updatedAt -> currentDate
        ).where.eq(RecruitmentDAO.column.id, id)
      }.update().apply()

      RecruitmentOccupationDAO.deleteAll(id)
      RecruitmentSpecificTagDAO.deleteAll(id)

      data.occupationIds.foreach({ it => RecruitmentOccupationDAO.create(id, it) })
      data.specificTagIds.foreach({ it => RecruitmentSpecificTagDAO.create(id, it) })

      true
    }
  }

  def convertFromTmp(id: Int, tmp: TmpRecruitment)(implicit s: DBSession = AutoSession): Boolean = {
    val currentDate = DateTime.now

    DB localTx { implicit s =>
      withSQL {
        update(RecruitmentDAO).set(
          column.statusId -> Recruitment.Status.Open.id,
          column.displayOccupationId -> tmp.base.display.occupation.id,
          column.displayPaymentTypeId -> tmp.base.display.paymentType.id,
          column.displayPaymentFrom -> tmp.base.display.paymentFrom,
          column.displayPaymentTo -> tmp.base.display.paymentTo,
          column.title -> tmp.base.info.title,
          column.pr -> tmp.base.info.pr,
          column.workInfo -> tmp.base.info.work,
          column.paymentInfo -> tmp.base.info.payment,
          column.workingHoursInfo -> tmp.base.info.workingHours,
          column.holidayInfo -> tmp.base.info.holiday,
          column.requirementInfo -> tmp.base.info.requirement,
          column.treatmentInfo -> tmp.base.info.treatment,
          column.entryMethodInfo -> tmp.base.info.entryMethod,
          column.lineUrl -> tmp.base.info.lineUrl,
          column.updatedAt -> currentDate
        ).where.eq(RecruitmentDAO.column.id, id)
      }.update().apply()

      RecruitmentOccupationDAO.deleteAll(id)
      RecruitmentSpecificTagDAO.deleteAll(id)
      RecruitmentPhotoDAO.deleteAll(id)

      tmp.base.occupations.foreach({ it => RecruitmentOccupationDAO.create(id, it.id) })
      tmp.base.tags.foreach({ it => RecruitmentSpecificTagDAO.create(id, it.id) })
      tmp.base.photos.foreach({ it => RecruitmentPhotoDAO.create(id, it.resourceName) })

      TmpRecruitmentOccupationDAO.deleteAll(tmp.base.id)
      TmpRecruitmentSpecificTagDAO.deleteAll(tmp.base.id)
      TmpRecruitmentPhotoDAO.deleteAll(tmp.base.id)
      TmpRecruitmentDAO.destroy(tmp.base.id)

      true
    }
  }

  def updateStatus(id: Int, status: Recruitment.Status)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      update(RecruitmentDAO).set(column.statusId -> status.id)
        .where
        .eq(RecruitmentDAO.column.id, id)
    }.update().apply()

    true
  }
}