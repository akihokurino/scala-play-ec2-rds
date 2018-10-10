package repositories

import com.google.inject.ImplementedBy
import infrastructure.PrefectureDAO
import models.Prefecture

@ImplementedBy(classOf[PrefectureRepositoryImpl])
trait PrefectureRepository {
  def fetchAll(): List[Prefecture]
  def fetch(id: Int): Option[Prefecture]
}

class PrefectureRepositoryImpl extends PrefectureRepository {
  def fetchAll(): List[Prefecture] = PrefectureDAO.fetchAll()

  def fetch(id: Int): Option[Prefecture] = PrefectureDAO.fetchById(id)
}

object PrefectureRepositoryImpl {

}
