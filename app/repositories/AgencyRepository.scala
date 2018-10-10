package repositories

import com.google.inject.ImplementedBy
import infrastructure.AgencyDAO
import models.Agency
import requests.CreateAgencyRequest

@ImplementedBy(classOf[AgencyRepositoryImpl])
trait AgencyRepository {
  def fetchAll(): List[Agency]
  def fetch(id: Int): Option[Agency]
  def create(data: CreateAgencyRequest): Agency
}

class AgencyRepositoryImpl extends AgencyRepository {
  def fetchAll(): List[Agency] = AgencyDAO.fetchAll()

  def fetch(id: Int): Option[Agency] = AgencyDAO.fetchById(id)

  def create(data: CreateAgencyRequest): Agency = AgencyDAO.fetchById(AgencyDAO.create(data)).get
}

object AgencyRepositoryImpl {

}
