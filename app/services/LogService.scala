package services

import javax.inject.Inject
import com.google.inject.ImplementedBy
import models.DailyBillingLog._
import models.DailyEntryLog._
import models.{DailyBillingLog, DailyEntryLog}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import play.api.Configuration
import play.api.libs.json.Json
import utils.DateUtil
import utils.MongoUtil._

@ImplementedBy(classOf[LogServiceImpl])
trait LogService {
  def calcLastWeekEntryNum(): Option[Int]
  def calcLastWeekBillingAmount(): Option[Int]
}

class LogServiceImpl @Inject()(configuration: Configuration) extends LogService {
  val mongoClient: MongoClient = MongoClient(configuration.getString("mongo.domain").get)
  val database: MongoDatabase = mongoClient.getDatabase(configuration.getString("mongo.dbname").get)

  def calcLastWeekEntryNum(): Option[Int] = {
    val range = DateUtil.lastWeekRange()
    val col: MongoCollection[Document] = database.getCollection("daily_entry_log")

    if (col.find(Document("date" -> range(0))).results.isEmpty) {
      return None
    }

    Some(range.map({ it =>
      col.find(Document("date" -> it)).results.map({ it =>
        Json.parse(it.toJson()).validate[DailyEntryLog].get
      }).toList.foldLeft(0) { (acc, x) => acc + x.value.toInt }
    }).sum)
  }

  def calcLastWeekBillingAmount(): Option[Int] = {
    val range = DateUtil.lastWeekRange()
    val col: MongoCollection[Document] = database.getCollection("daily_billing_log")

    if (col.find(Document("date" -> range(0))).results.isEmpty) {
      return None
    }

    Some(range.map({ it =>
      col.find(Document("date" -> it)).results.map({ it =>
        Json.parse(it.toJson()).validate[DailyBillingLog].get
      }).toList.foldLeft(0) { (acc, x) => acc + x.value.toInt }
    }).sum)
  }
}

object LogServiceImpl {

}