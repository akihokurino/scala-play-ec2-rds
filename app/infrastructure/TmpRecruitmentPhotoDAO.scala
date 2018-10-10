package infrastructure

import models.Recruitment
import scalikejdbc._

case class TmpRecruitmentPhotoDAO(id: Int, tmpRecruitmentId: Int, resourceName: String) {
  def to(): Recruitment.Photo = Recruitment.Photo(id, resourceName)
}

object TmpRecruitmentPhotoDAO extends SQLSyntaxSupport[TmpRecruitmentPhotoDAO] {
  override val tableName = "tmp_recruitment_photos"

  def apply(r: ResultName[TmpRecruitmentPhotoDAO])(rs: WrappedResultSet) =
    new TmpRecruitmentPhotoDAO(
      rs.int(r.id),
      rs.int(r.tmpRecruitmentId),
      rs.string(r.resourceName)
    )

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Recruitment.Photo] = {
    val _recruitmentPhotoDAO = TmpRecruitmentPhotoDAO.syntax

    withSQL {
      select.from(TmpRecruitmentPhotoDAO as _recruitmentPhotoDAO)
        .where
        .eq(_recruitmentPhotoDAO.id, id)
    }.map { rs =>
      TmpRecruitmentPhotoDAO(_recruitmentPhotoDAO.resultName)(rs).to()
    }.single().apply()
  }

  def fetchAllOfTmpRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[Recruitment.Photo] = {
    val _recruitmentPhotoDAO = TmpRecruitmentPhotoDAO.syntax

    withSQL {
      select.from(TmpRecruitmentPhotoDAO as _recruitmentPhotoDAO)
        .where
        .eq(_recruitmentPhotoDAO.tmpRecruitmentId, recruitmentId)
        .orderBy(_recruitmentPhotoDAO.id)
        .asc
    }.map { rs =>
      TmpRecruitmentPhotoDAO(_recruitmentPhotoDAO.resultName)(rs).to()
    }.list().apply()
  }

  def create(recruitmentId: Int, resourceName: String)(implicit s: DBSession = AutoSession): Int = {
    withSQL {
      insert.into(TmpRecruitmentPhotoDAO).namedValues(
        column.tmpRecruitmentId -> recruitmentId,
        column.resourceName -> resourceName
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(TmpRecruitmentPhotoDAO).where.eq(column.tmpRecruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}
