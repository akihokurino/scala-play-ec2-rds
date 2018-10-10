package services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.redis._
import models.Recruitment
import play.api.Configuration

import scala.util._

@ImplementedBy(classOf[TaskQueueServiceImpl])
trait TaskQueueService {
  def queueSearchIndex(recruitment: Recruitment)
}

class TaskQueueServiceImpl @Inject()(configuration: Configuration) extends TaskQueueService {
  private val client = new RedisClient(
    configuration.getString("redis.domain").get,
    configuration.getString("redis.port").get.toInt)

  def queueSearchIndex(recruitment: Recruitment): Unit = {
    val current = System.currentTimeMillis()
    val taskKey = s"search-index-${Random.alphanumeric.take(10).mkString}-${current}"
    client.hmset(taskKey, Map("recruitmentId" -> recruitment.id, "timestamp" -> current))
  }
}

object TaskQueueServiceImpl {

}
