package models

import java.io.File

import infrastructure.{RecruitmentDAO, RecruitmentPhotoDAO}
import models.Recruitment.Display
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import requests.UpdateRecruitmentRequest
import services.{StorageService, TaskQueueService}
import utils.StorageUtil

case class Recruitment(id: Int,
                       storeId: Int, // 実装観点から必要
                       status: Recruitment.Status,
                       display: Display,
                       info: Recruitment.Info,
                       createdAt: String,
                       updatedAt: String,
                       occupations: List[Occupation],
                       photos: List[Recruitment.Photo],
                       tags: List[SpecificTag]) {

  val isFinished: Boolean = status == Recruitment.Status.EarlyClose || status == Recruitment.Status.Close

  def update(data: UpdateRecruitmentRequest, taskQueueService: TaskQueueService): Recruitment = {
    RecruitmentDAO.edit(id, data)
    val self = RecruitmentDAO.fetchById(id).get
    taskQueueService.queueSearchIndex(self)
    self
  }

  def approve(): Recruitment = updateStatus(Recruitment.Status.WillOpen)

  def reject(): Recruitment = updateStatus(Recruitment.Status.Rejected)

  def earlyClose(): Recruitment = updateStatus(Recruitment.Status.EarlyClose)

  def createPhoto(data: MultipartFormData.FilePart[Files.TemporaryFile], s3Service: StorageService): Recruitment.Photo = {
    val resourceName = upload(data, s3Service)
    RecruitmentPhotoDAO.fetchById(RecruitmentPhotoDAO.create(id, resourceName)).get
  }

  def upload(data: MultipartFormData.FilePart[Files.TemporaryFile], s3Service: StorageService): String = {
    val filename = data.filename
    val currentTime = System.currentTimeMillis()
    val localFile = new File(s"/tmp/$filename")

    data.ref.moveTo(localFile)

    s3Service.upload(
      s"recruitments/${this.id}/thumbnails/$currentTime-$filename",
      localFile
    )
  }

  def deleteAllPhotos(): Recruitment = {
    RecruitmentPhotoDAO.deleteAll(id)
    copy(photos = List.empty)
  }

  private def updateStatus(newStatus: Recruitment.Status): Recruitment = {
    RecruitmentDAO.updateStatus(id, newStatus)
    copy(status = newStatus)
  }
}

object Recruitment {
  sealed abstract class  Status(val id: Int) {
    val text: String
  }

  object Status {
    def from(id: Int): Status = {
      id match {
        case 1 => Requesting
        case 2 => Rejected
        case 3 => WillOpen
        case 4 => Open
        case 5 => Close
        case 6 => EarlyClose
        case 7 => Tmp
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object Requesting extends Status(1) {
      override val text: String = "申請中"
    }
    case object Rejected extends Status(2) {
      override val text: String = "拒否"
    }
    case object WillOpen extends Status(3) {
      override val text: String = "公開予定"
    }
    case object Open extends Status(4) {
      override val text: String = "公開中"
    }
    case object Close extends Status(5) {
      override val text: String = "公開終了"
    }
    case object EarlyClose extends Status(6) {
      override val text: String = "早期終了"
    }
    case object Tmp extends Status(7) {
      override val text: String = "下書き"
    }
  }

  sealed abstract class  PaymentType(val id: Int) {
    val text: String
  }

  object PaymentType {
    def from(id: Int): PaymentType = {
      id match {
        case 1 => HourlyWage
        case 2 => DailyWage
        case 3 => MonthlySalary
        case _ => throw new RuntimeException("invalid value")
      }
    }

    case object HourlyWage extends PaymentType(1) {
      override val text: String = "時給"
    }
    case object DailyWage extends PaymentType(2) {
      override val text: String = "日給"
    }
    case object MonthlySalary extends PaymentType(3) {
      override val text: String = "月給"
    }
  }

  case class Photo(id: Int, resourceName: String) {
    val resourceFullPath: String = StorageUtil.createURL(this.resourceName)
  }

  case class Display(occupation: Occupation,
                     paymentType: PaymentType,
                     paymentFrom: Int,
                     paymentTo: Option[Int])

  case class Info(title: String,
                  pr: String,
                  work: String,
                  payment: String,
                  workingHours: String,
                  holiday: String,
                  requirement: String,
                  treatment: String,
                  entryMethod: String,
                  lineUrl: String)

  val paymentTypes: List[PaymentType] = List(PaymentType.HourlyWage, PaymentType.DailyWage, PaymentType.MonthlySalary)
}
