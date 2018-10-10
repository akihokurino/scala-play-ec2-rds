package infrastructure

import models.{Area, Prefecture}
import scalikejdbc._

case class AreaDAO(id: Int, prefectureId: Int, name: String) {
  def to(): Area = Area(id, name)
}

object AreaDAO extends SQLSyntaxSupport[AreaDAO] {
  override val tableName = "areas"

  def apply(r: ResultName[AreaDAO])(rs: WrappedResultSet) =
    new AreaDAO(
      rs.int(r.id),
      rs.int(r.prefectureId),
      rs.string(r.name)
    )

  def fetchAllOfPrefecture(prefecture: Prefecture)(implicit s: DBSession = AutoSession): List[Area] = {
    val _areaDAO = AreaDAO.syntax

    withSQL {
      select.from(AreaDAO as _areaDAO)
        .where
        .eq(_areaDAO.prefectureId, prefecture.id)
        .orderBy(_areaDAO.id)
        .asc
    }.map { rs =>
      AreaDAO(_areaDAO.resultName)(rs).to()
    }.list().apply()
  }
}
