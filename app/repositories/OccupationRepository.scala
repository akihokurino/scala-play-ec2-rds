package repositories

import com.google.inject.ImplementedBy
import infrastructure.OccupationDAO
import models.Occupation

@ImplementedBy(classOf[OccupationRepositoryImpl])
trait OccupationRepository {
  def fetchAll(): List[Occupation]
}

class OccupationRepositoryImpl extends OccupationRepository {
  def fetchAll(): List[Occupation] = OccupationDAO.fetchAll()
}

object OccupationRepositoryImpl {

}

