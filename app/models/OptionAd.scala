package models

import java.io.File

import infrastructure.OptionAdDAO
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import services.StorageService
import utils.StorageUtil

case class OptionAd(id: Int,
                    store: Store.Overview,
                    option: PublishedOption,
                    occupation: Occupation,
                    resourceName: String,
                    startDate: String,
                    endDate: String,
                    createdAt: String) {

  val resourceFullPath: String = StorageUtil.createURL(this.resourceName)

  def updateThumbnail(data: MultipartFormData.FilePart[Files.TemporaryFile], s3Service: StorageService): OptionAd = {
    val filename = data.filename
    val currentTime = System.currentTimeMillis()
    val localFile = new File(s"/tmp/$filename")

    data.ref.moveTo(localFile)

    val _resourceName = s3Service.upload(
      s"option_ads/${this.id}/thumbnails/$currentTime-$filename",
      localFile
    )

    val _ = OptionAdDAO.updateResource(id, _resourceName)
    copy(resourceName = _resourceName)
  }

  def delete(): Boolean = OptionAdDAO.destroy(id)
}

object OptionAd {

}

