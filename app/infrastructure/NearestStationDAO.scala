package infrastructure

import models.Store
import requests.CreateNearestStationRequest
import scalikejdbc._

case class NearestStationDAO(id: Int, storeId: Int, routeId: Int, stationId: Int) {
  def to(): Store.NearestStation = Store.NearestStation(id, routeId, stationId)
}

object NearestStationDAO extends SQLSyntaxSupport[NearestStationDAO] {
  override val tableName = "nearest_stations"

  def apply(r: ResultName[NearestStationDAO])(rs: WrappedResultSet) =
    new NearestStationDAO(
      rs.int(r.id),
      rs.int(r.storeId),
      rs.int(r.routeId),
      rs.int(r.stationId)
    )

  def fetchAllOfStore(storeId: Int)(implicit s: DBSession = AutoSession): List[Store.NearestStation] = {
    val _nearestStationDAO = NearestStationDAO.syntax

    withSQL {
      select.from(NearestStationDAO as _nearestStationDAO)
        .where
        .eq(_nearestStationDAO.storeId, storeId)
        .orderBy(_nearestStationDAO.id)
        .asc
    }.map { rs =>
      NearestStationDAO(_nearestStationDAO.resultName)(rs).to()
    }.list().apply()
  }

  def fetchById(id: Int)(implicit s: DBSession = AutoSession): Option[Store.NearestStation] = {
    val _nearestStationDAO = NearestStationDAO.syntax

    withSQL {
      select.from(NearestStationDAO as _nearestStationDAO)
        .where
        .eq(_nearestStationDAO.id, id)
    }.map { rs =>
      NearestStationDAO(_nearestStationDAO.resultName)(rs).to()
    }.single().apply()
  }

  def create(storeId: Int, data: CreateNearestStationRequest)(implicit s: DBSession = AutoSession): Int = {
    withSQL {
      insert.into(NearestStationDAO).namedValues(
        column.storeId -> storeId,
        column.routeId -> data.routeId,
        column.stationId -> data.stationId
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def deleteAll(storeId: Int)(implicit s: DBSession = AutoSession): Boolean = {
    withSQL {
      delete.from(NearestStationDAO).where.eq(column.storeId, storeId)
    }.update().apply()

    true
  }
}

