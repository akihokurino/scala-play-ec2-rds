package repositories

import com.google.inject.ImplementedBy
import infrastructure.QuestionDAO
import models.Question

@ImplementedBy(classOf[QuestionRepositoryImpl])
trait QuestionRepository {
  def fetch(id: Int): Option[Question]
}

class QuestionRepositoryImpl extends QuestionRepository {
  def fetch(id: Int): Option[Question] = QuestionDAO.fetchById(id)
}

object QuestionRepositoryImpl {

}
