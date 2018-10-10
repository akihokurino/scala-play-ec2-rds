import javax.inject.{Inject, Singleton}

import play.api.Configuration
import scalikejdbc._

@Singleton
class Initialize @Inject()(configuration: Configuration) {

  // Initialize DB
  Class.forName("com.mysql.jdbc.Driver")
  ConnectionPool.singleton(
    configuration.getString("db.default.url").get,
    configuration.getString("db.default.username").get,
    configuration.getString("db.default.password").get)
}

