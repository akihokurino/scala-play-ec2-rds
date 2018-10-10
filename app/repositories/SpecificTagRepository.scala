package repositories

import com.google.inject.ImplementedBy
import infrastructure.SpecificTagDAO
import models.SpecificTag

@ImplementedBy(classOf[SpecificTagRepositoryImpl])
trait SpecificTagRepository {
  def fetchAll(): List[SpecificTag]
}

class SpecificTagRepositoryImpl extends SpecificTagRepository {
  def fetchAll(): List[SpecificTag] = SpecificTagDAO.fetchAll()
}

object SpecificTagRepositoryImpl {

}
