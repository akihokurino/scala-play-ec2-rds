package models

import infrastructure.{RecruitmentDAO, TmpRecruitmentDAO, TmpRecruitmentPhotoDAO}
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import services.{StorageService, TaskQueueService}

case class TmpRecruitment(base: Recruitment) {
  def createPhoto(data: MultipartFormData.FilePart[Files.TemporaryFile], s3Service: StorageService): Recruitment.Photo = {
    val resourceName = base.upload(data, s3Service)
    TmpRecruitmentPhotoDAO.fetchById(TmpRecruitmentPhotoDAO.create(base.id, resourceName)).get
  }

  def convert(to: Recruitment, taskQueueService: TaskQueueService): Recruitment = {
    val _ = RecruitmentDAO.convertFromTmp(to.id, this)
    taskQueueService.queueSearchIndex(to)
    RecruitmentDAO.fetchById(to.id).get
  }

  def deleteAll(): Unit = {
    val _ = TmpRecruitmentDAO.deleteAll(base.id)
  }
}

object TmpRecruitment {

}