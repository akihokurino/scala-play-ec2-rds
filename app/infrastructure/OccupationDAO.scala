package infrastructure

import infrastructure.intermediate.{EntryOccupationDAO, RecruitmentOccupationDAO, TmpRecruitmentOccupationDAO}
import models.Occupation
import scalikejdbc._

case class OccupationDAO(id: Int, name: String) {
  def to(): Occupation = Occupation(id, name)
}

object OccupationDAO extends SQLSyntaxSupport[OccupationDAO] {
  override val tableName = "occupations"

  def apply(r: ResultName[OccupationDAO])(rs: WrappedResultSet) =
    new OccupationDAO(
      rs.int(r.id),
      rs.string(r.name)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[Occupation] = {
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(OccupationDAO as _occupationDAO)
        .orderBy(_occupationDAO.id)
        .asc
    }.map { rs =>
      OccupationDAO(_occupationDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllOfRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[Occupation] = {
    val _occupationDAO = OccupationDAO.syntax
    val _intermediate = RecruitmentOccupationDAO.syntax

    withSQL {
      select.from(RecruitmentOccupationDAO as _intermediate)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_intermediate.occupationId, _occupationDAO.id)
        .where
        .eq(_intermediate.recruitmentId, recruitmentId)
        .orderBy(_occupationDAO.id)
        .asc
    }.map { rs =>
      OccupationDAO(_occupationDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllOfTmpRecruitment(recruitmentId: Int)(implicit s: DBSession = AutoSession): List[Occupation] = {
    val _occupationDAO = OccupationDAO.syntax
    val _intermediate = TmpRecruitmentOccupationDAO.syntax

    withSQL {
      select.from(TmpRecruitmentOccupationDAO as _intermediate)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_intermediate.occupationId, _occupationDAO.id)
        .where
        .eq(_intermediate.tmpRecruitmentId, recruitmentId)
        .orderBy(_occupationDAO.id)
        .asc
    }.map { rs =>
      OccupationDAO(_occupationDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchAllOfEntry(entryId: Int)(implicit s: DBSession = AutoSession): List[Occupation] = {
    val _occupationDAO = OccupationDAO.syntax
    val _intermediate = EntryOccupationDAO.syntax

    withSQL {
      select.from(EntryOccupationDAO as _intermediate)
        .innerJoin(OccupationDAO as _occupationDAO)
        .on(_intermediate.occupationId, _occupationDAO.id)
        .where
        .eq(_intermediate.entryId, entryId)
        .orderBy(_occupationDAO.id)
        .asc
    }.map { rs =>
      OccupationDAO(_occupationDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Occupation] = {
    val _occupationDAO = OccupationDAO.syntax

    withSQL {
      select.from(OccupationDAO as _occupationDAO)
        .where
        .eq(_occupationDAO.id, id)
    }.map { rs =>
      OccupationDAO(_occupationDAO.resultName)(rs).to()
    }.single().apply()
  }
}