package utils

import com.typesafe.config.ConfigFactory
import play.api.Configuration

object StorageUtil {
  def createURL(path: String): String = Configuration(ConfigFactory.load()).getString("storage.domain").get + path
}
