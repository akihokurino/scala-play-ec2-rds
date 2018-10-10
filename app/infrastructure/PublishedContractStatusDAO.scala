package infrastructure

import scalikejdbc._

case class PublishedContractStatusDAO(id: Int, name: String)

object PublishedContractStatusDAO extends SQLSyntaxSupport[PublishedContractStatusDAO] {
  override val tableName = "published_contract_statuses"

  def apply(r: ResultName[PublishedContractStatusDAO])(rs: WrappedResultSet) =
    new PublishedContractStatusDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}