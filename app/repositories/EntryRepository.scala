package repositories

import com.google.inject.ImplementedBy
import infrastructure.EntryDAO
import models.Entry

@ImplementedBy(classOf[EntryRepositoryImpl])
trait EntryRepository {
  def fetchAll(): List[Entry]
}

class EntryRepositoryImpl extends EntryRepository {
  def fetchAll(): List[Entry] = EntryDAO.fetchAll()
}

object EntryRepositoryImpl {

}
