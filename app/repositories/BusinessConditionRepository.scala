package repositories

import com.google.inject.ImplementedBy
import infrastructure.BusinessConditionDAO
import models.BusinessCondition

@ImplementedBy(classOf[BusinessConditionRepositoryImpl])
trait BusinessConditionRepository {
  def fetchAll(): List[BusinessCondition]
}

class BusinessConditionRepositoryImpl extends BusinessConditionRepository {
  def fetchAll(): List[BusinessCondition] = BusinessConditionDAO.fetchAll()
}

object BusinessConditionRepositoryImpl {

}
