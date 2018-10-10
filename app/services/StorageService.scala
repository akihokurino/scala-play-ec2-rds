package services

import java.io.File
import javax.inject.Inject

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}
import com.google.inject.ImplementedBy
import play.api.Configuration

@ImplementedBy(classOf[StorageServiceImpl])
trait StorageService {
  def upload(filePath: String, localFile: File): String
}

class StorageServiceImpl @Inject()(configuration: Configuration) extends StorageService {
  private val client = new AmazonS3Client(new BasicAWSCredentials("AKIAIYAYPOXJZWTFWLCQ", "uWweCyD0Me4o7zamTqfBsCHnfdSBHj429mGn9xh1"))

  def upload(filePath: String, localFile: File): String = {
    val upReq = new PutObjectRequest(configuration.getString("storage.bucket").get, filePath, localFile)
      .withCannedAcl(CannedAccessControlList.PublicRead)
    client.putObject(upReq)
    filePath
  }
}

object StorageServiceImpl {

}

