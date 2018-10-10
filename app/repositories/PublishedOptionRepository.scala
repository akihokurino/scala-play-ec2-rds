package repositories

import com.google.inject.ImplementedBy
import infrastructure.PublishedOptionDAO
import models.PublishedOption

@ImplementedBy(classOf[PublishedOptionRepositoryImpl])
trait PublishedOptionRepository {
  def fetchAll(): List[PublishedOption]
}

class PublishedOptionRepositoryImpl extends PublishedOptionRepository {
  def fetchAll(): List[PublishedOption] = PublishedOptionDAO.fetchAll()
}

object PublishedOptionRepositoryImpl {

}
