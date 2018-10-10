package infrastructure

import models.{Area, Prefecture}
import scalikejdbc._

case class PrefectureDAO(id: Int, regionId: Int, name: String, nameKana: String) {
  def to(): Prefecture = Prefecture(id, name, List.empty)

  def to(areas: List[Area]): Prefecture = Prefecture(id, name, areas)
}

object PrefectureDAO extends SQLSyntaxSupport[PrefectureDAO] {
  override val tableName = "prefectures"

  def apply(r: ResultName[PrefectureDAO])(rs: WrappedResultSet) =
    new PrefectureDAO(
      rs.int(r.id),
      rs.int(r.regionId),
      rs.string(r.name),
      rs.string(r.nameKana)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[Prefecture] = {
    val _prefectureDAO = PrefectureDAO.syntax

    withSQL {
      select.from(PrefectureDAO as _prefectureDAO)
        .orderBy(_prefectureDAO.id)
        .asc
    }.map { rs =>
      val prefectureDAO = PrefectureDAO(_prefectureDAO.resultName)(rs)
      val areas = AreaDAO.fetchAllOfPrefecture(prefectureDAO.to())
      prefectureDAO.to(areas)
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Prefecture] = {
    val _prefectureDAO = PrefectureDAO.syntax

    withSQL {
      select.from(PrefectureDAO as _prefectureDAO)
        .where
        .eq(_prefectureDAO.id, id)
    }.map { rs =>
      val prefectureDAO = PrefectureDAO(_prefectureDAO.resultName)(rs)
      val areas = AreaDAO.fetchAllOfPrefecture(prefectureDAO.to())
      prefectureDAO.to(areas)
    }.single().apply()
  }
}