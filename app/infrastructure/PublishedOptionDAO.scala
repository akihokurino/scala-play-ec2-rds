package infrastructure

import models.PublishedOption
import scalikejdbc._

case class PublishedOptionDAO(id: Int, name: String) {
  def to(): PublishedOption = PublishedOption(id, name)
}

object PublishedOptionDAO extends SQLSyntaxSupport[PublishedOptionDAO] {
  override val tableName = "published_options"

  def apply(r: ResultName[PublishedOptionDAO])(rs: WrappedResultSet) =
    new PublishedOptionDAO(
      rs.int(r.id),
      rs.string(r.name)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[PublishedOption] = {
    val _publishedOptionDAO = PublishedOptionDAO.syntax

    withSQL {
      select.from(PublishedOptionDAO as _publishedOptionDAO)
        .orderBy(_publishedOptionDAO.id)
        .asc
    }.map { rs =>
      PublishedOptionDAO(_publishedOptionDAO.resultName)(rs).to()
    }.list().apply()
  }
}