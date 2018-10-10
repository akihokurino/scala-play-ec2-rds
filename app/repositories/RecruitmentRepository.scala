package repositories

import javax.inject.Inject

import com.google.inject.ImplementedBy
import infrastructure.RecruitmentDAO
import models.Recruitment
import requests.CreateRecruitmentRequest
import services.TaskQueueService

@ImplementedBy(classOf[RecruitmentRepositoryImpl])
trait RecruitmentRepository {
  def fetchAllOf(status: Recruitment.Status): List[Recruitment]
  def fetch(id: Int): Option[Recruitment]
  def create(data: CreateRecruitmentRequest): Recruitment
}

class RecruitmentRepositoryImpl @Inject()(taskQueueService: TaskQueueService) extends RecruitmentRepository {
  def fetchAllOf(status: Recruitment.Status): List[Recruitment] = RecruitmentDAO.fetchAllOfStatus(status)

  def fetch(id: Int): Option[Recruitment] = RecruitmentDAO.fetchById(id)

  def create(data: CreateRecruitmentRequest): Recruitment = {
    val insertId = RecruitmentDAO.create(data)
    val result = fetch(insertId).get
    taskQueueService.queueSearchIndex(result)
    result
  }
}

object RecruitmentRepositoryImpl {

}
