package repositories

import com.google.inject.ImplementedBy
import infrastructure.TmpRecruitmentDAO
import models.TmpRecruitment
import requests.CreateRecruitmentRequest

@ImplementedBy(classOf[TmpRecruitmentRepositoryImpl])
trait TmpRecruitmentRepository {
  def fetch(id: Int): Option[TmpRecruitment]
  def fetchOfStore(storeId: Int): Option[TmpRecruitment]
  def create(data: CreateRecruitmentRequest): TmpRecruitment
}

class TmpRecruitmentRepositoryImpl extends TmpRecruitmentRepository {
  def fetch(id: Int): Option[TmpRecruitment] = TmpRecruitmentDAO.fetchById(id)

  def fetchOfStore(storeId: Int): Option[TmpRecruitment] = TmpRecruitmentDAO.fetchByStoreId(storeId)

  def create(data: CreateRecruitmentRequest): TmpRecruitment = {
    val insertId = TmpRecruitmentDAO.create(data)
    fetch(insertId).get
  }
}

object TmpRecruitmentRepositoryImpl {

}
