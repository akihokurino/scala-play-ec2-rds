package repositories

import com.google.inject.ImplementedBy
import infrastructure.PublishedContractDAO
import models.PublishedContract

@ImplementedBy(classOf[PublishedContractRepositoryImpl])
trait PublishContractRepository {
  def fetchAll(): List[PublishedContract.WithStore]
}

class PublishedContractRepositoryImpl extends PublishContractRepository {
  def fetchAll(): List[PublishedContract.WithStore] = PublishedContractDAO.fetchAll()
}

object PublishedContractRepositoryImpl {

}
