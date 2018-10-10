package infrastructure

import infrastructure.intermediate.{TmpRecruitmentOccupationDAO, TmpRecruitmentSpecificTagDAO}
import models.{Occupation, Recruitment, TmpRecruitment}
import requests.CreateRecruitmentRequest
import scalikejdbc._

case class TmpRecruitmentDAO(id: Int,
                             storeId: Int,
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
                             lineUrl: String) {

  private def to(occupation: Occupation): TmpRecruitment = {
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
      occupation,
      Recruitment.PaymentType.from(displayPaymentTypeId),
      displayPaymentFrom,
      displayPaymentTo
    )

    val occupations = OccupationDAO.fetchAllOfTmpRecruitment(id)
    val photos = TmpRecruitmentPhotoDAO.fetchAllOfTmpRecruitment(id)
    val tags = SpecificTagDAO.fetchAllOfTmpRecruitment(id)

    TmpRecruitment(Recruitment(
      id,
      storeId,
      Recruitment.Status.Tmp,
      display,
      info,
      "",
      "",
      occupations,
      photos,
      tags))
  }

  def to(displayOccupationDAO: OccupationDAO): TmpRecruitment = to(displayOccupationDAO.to())

  // Not unique table/alias のためjoinはしていない)
  def to(): TmpRecruitment = to(OccupationDAO.fetchById(displayOccupationId).get)
}

object TmpRecruitmentDAO extends SQLSyntaxSupport[TmpRecruitmentDAO] {
  override val tableName = "tmp_recruitments"

  def apply(r: ResultName[TmpRecruitmentDAO])(rs: WrappedResultSet) =
    new TmpRecruitmentDAO(
      rs.int(r.id),
      rs.int(r.storeId),
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
      rs.string(r.lineUrl)
    )

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[TmpRecruitment] = {
    val _recruitmentDAO = TmpRecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(TmpRecruitmentDAO as _recruitmentDAO)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
        .where
        .eq(_recruitmentDAO.id, id)
    }.map { rs =>
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      TmpRecruitmentDAO(_recruitmentDAO.resultName)(rs).to(occupationDAO)
    }.single().apply()
  }

  def fetchByStoreId(storeId: Int)(implicit s: DBSession = AutoSession): Option[TmpRecruitment] = {
    val _recruitmentDAO = TmpRecruitmentDAO.syntax
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(TmpRecruitmentDAO as _recruitmentDAO)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_recruitmentDAO.displayOccupationId, _occupationDAO.id)
        .where
        .eq(_recruitmentDAO.storeId, storeId)
    }.map { rs =>
      val occupationDAO = OccupationDAO(_occupationDAO.resultName)(rs)
      TmpRecruitmentDAO(_recruitmentDAO.resultName)(rs).to(occupationDAO)
    }.single().apply()
  }

  def create(data: CreateRecruitmentRequest)(implicit s: DBSession = AutoSession): Int = {
    DB localTx { implicit s =>
      val insertId: Int = withSQL {
        insert.into(TmpRecruitmentDAO).namedValues(
          column.storeId -> data.storeId,
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
          column.lineUrl -> data.lineUrl
        )
      }.updateAndReturnGeneratedKey().apply().toInt

      data.occupationIds.foreach({ it => TmpRecruitmentOccupationDAO.create(insertId, it) })
      data.specificTagIds.foreach({ it => TmpRecruitmentSpecificTagDAO.create(insertId, it) })

      insertId
    }
  }

  def destroy(id: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(TmpRecruitmentDAO).where.eq(column.id, id)
    }.update().apply()

    true
  }

  def deleteAll(id: Int)(implicit s: DBSession = AutoSession): Boolean = {
    DB localTx { implicit s =>
      TmpRecruitmentOccupationDAO.deleteAll(id)
      TmpRecruitmentSpecificTagDAO.deleteAll(id)
      TmpRecruitmentPhotoDAO.deleteAll(id)
      TmpRecruitmentDAO.destroy(id)

      true
    }
  }
}
