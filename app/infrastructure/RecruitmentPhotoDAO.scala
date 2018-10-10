package infrastructure

import models.Recruitment
import scalikejdbc._

case class RecruitmentPhotoDAO(id: Int, recruitmentId: Int, resourceName: String) {
  def to(): Recruitment.Photo = Recruitment.Photo(id, resourceName)
}

object RecruitmentPhotoDAO extends SQLSyntaxSupport[RecruitmentPhotoDAO] {
  override val tableName = "recruitment_photos"

  def apply(r: ResultName[RecruitmentPhotoDAO])(rs: WrappedResultSet) =
    new RecruitmentPhotoDAO(
      rs.int(r.id),
      rs.int(r.recruitmentId),
      rs.string(r.resourceName)
    )

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Recruitment.Photo] = {
    val _recruitmentPhotoDAO = RecruitmentPhotoDAO.syntax

    withSQL {
      select.from(RecruitmentPhotoDAO as _recruitmentPhotoDAO)
        .where
        .eq(_recruitmentPhotoDAO.id, id)
    }.map { rs =>
      RecruitmentPhotoDAO(_recruitmentPhotoDAO.resultName)(rs).to()
    }.single().apply()
  }

  def fetchAllOfRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[Recruitment.Photo] = {
    val _recruitmentPhotoDAO = RecruitmentPhotoDAO.syntax

    withSQL {
      select.from(RecruitmentPhotoDAO as _recruitmentPhotoDAO)
        .where
        .eq(_recruitmentPhotoDAO.recruitmentId, recruitmentId)
        .orderBy(_recruitmentPhotoDAO.id)
        .asc
    }.map { rs =>
      RecruitmentPhotoDAO(_recruitmentPhotoDAO.resultName)(rs).to()
    }.list().apply()
  }

  def create(recruitmentId: Int, resourceName: String)(implicit s: DBSession = AutoSession): Int = {
    withSQL {
      insert.into(RecruitmentPhotoDAO).namedValues(
        column.recruitmentId -> recruitmentId,
        column.resourceName -> resourceName
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def deleteAll(recruitmentId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(RecruitmentPhotoDAO).where.eq(column.recruitmentId, recruitmentId)
    }.update().apply()

    true
  }
}
