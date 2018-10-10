package repositories

import com.google.inject.ImplementedBy
import infrastructure.OptionAdDAO
import models.OptionAd
import requests.CreateOptionAdRequest

@ImplementedBy(classOf[OptionAdRepositoryImpl])
trait OptionAdRepository {
  def fetchAll(): List[OptionAd]
  def fetch(id: Int): Option[OptionAd]
  def create(data: CreateOptionAdRequest): OptionAd
}

class OptionAdRepositoryImpl extends OptionAdRepository {
  def fetchAll(): List[OptionAd] = OptionAdDAO.fetchAll()

  def fetch(id: Int): Option[OptionAd] = OptionAdDAO.fetchById(id)

  def create(data: CreateOptionAdRequest): OptionAd = OptionAdDAO.fetchById(OptionAdDAO.create(data)).get
}

object OptionAdRepositoryImpl {

}
