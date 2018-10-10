package infrastructure

import models._
import scalikejdbc._

case class PublishedPlanDAO(id: Int, name: String) {
  def to(): PublishedPlan = PublishedPlan(id, name)
}

object PublishedPlanDAO extends SQLSyntaxSupport[PublishedPlanDAO] {
  override val tableName = "published_plans"

  def apply(r: ResultName[PublishedPlanDAO])(rs: WrappedResultSet) =
    new PublishedPlanDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}