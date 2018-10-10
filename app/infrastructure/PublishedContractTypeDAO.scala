package infrastructure

import scalikejdbc._

case class PublishedContractTypeDAO(id: Int, name: String)

object PublishedContractTypeDAO extends SQLSyntaxSupport[PublishedContractTypeDAO] {
  override val tableName = "published_contract_types"

  def apply(r: ResultName[PublishedContractTypeDAO])(rs: WrappedResultSet) =
    new PublishedContractTypeDAO(
      rs.int(r.id),
      rs.string(r.name)
    )
}