package infrastructure

import infrastructure.intermediate.{RecruitmentSpecificTagDAO, TmpRecruitmentSpecificTagDAO}
import models.SpecificTag
import scalikejdbc._

case class SpecificTagDAO(id: Int, name: String) {
  def to(): SpecificTag = SpecificTag(id, name)
}

object SpecificTagDAO extends SQLSyntaxSupport[SpecificTagDAO] {
  override val tableName = "specific_tags"

  def apply(r: ResultName[SpecificTagDAO])(rs: WrappedResultSet) =
    new SpecificTagDAO(
      rs.int(r.id),
      rs.string(r.name)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[SpecificTag] = {
    val _specificTagDAO = SpecificTagDAO.syntax

    withSQL {
      select.from(SpecificTagDAO as _specificTagDAO)
        .orderBy(_specificTagDAO.id)
        .asc
    }.map { rs =>
      SpecificTagDAO(_specificTagDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllOfRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[SpecificTag] = {
    val _specificTagDAO = SpecificTagDAO.syntax
    val _intermediate = RecruitmentSpecificTagDAO.syntax

    withSQL {
      select.from(RecruitmentSpecificTagDAO as _intermediate)
        .innerJoin(SpecificTagDAO as _specificTagDAO)
        .on(_intermediate.specificTagId, _specificTagDAO.id)
        .where
        .eq(_intermediate.recruitmentId, recruitmentId)
        .orderBy(_specificTagDAO.id)
        .asc
    }.map { rs =>
      SpecificTagDAO(_specificTagDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllOfTmpRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[SpecificTag] = {
    val _specificTagDAO = SpecificTagDAO.syntax
    val _intermediate = TmpRecruitmentSpecificTagDAO.syntax

    withSQL {
      select.from(TmpRecruitmentSpecificTagDAO as _intermediate)
        .innerJoin(SpecificTagDAO as _specificTagDAO)
        .on(_intermediate.specificTagId, _specificTagDAO.id)
        .where
        .eq(_intermediate.tmpRecruitmentId, recruitmentId)
        .orderBy(_specificTagDAO.id)
        .asc
    }.map { rs =>
      SpecificTagDAO(_specificTagDAO.resultName)(rs).to()
    }.list().apply()
  }
}