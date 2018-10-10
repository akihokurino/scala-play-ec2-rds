package infrastructure

import models.BusinessCondition
import scalikejdbc._

case class BusinessConditionDAO(id: Int, name: String) {
  def to(): BusinessCondition = BusinessCondition(id, name)
}

object BusinessConditionDAO extends SQLSyntaxSupport[BusinessConditionDAO] {
  override val tableName = "business_conditions"

  def apply(r: ResultName[BusinessConditionDAO])(rs: WrappedResultSet) =
    new BusinessConditionDAO(
      rs.int(r.id),
      rs.string(r.name)
    )

  def fetchAll()(implicit s: DBSession = AutoSession): List[BusinessCondition] = {
    val _businessConditionDAO = BusinessConditionDAO.syntax

    withSQL {
      select.from(BusinessConditionDAO as _businessConditionDAO)
        .orderBy(_businessConditionDAO.id)
        .asc
    }.map { rs =>
      BusinessConditionDAO(_businessConditionDAO.resultName)(rs).to()
    }.list().apply()
  }
}