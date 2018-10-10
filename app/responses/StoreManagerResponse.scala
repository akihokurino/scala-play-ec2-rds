package responses

import models.Store
import spray.json._

case class StoreManagerResponse(name: String,
                                nameKana: String,
                                email: String,
                                subEmail: String)

object StoreManagerProtocol extends DefaultJsonProtocol {
  implicit def storeManagerFormat: RootJsonFormat[StoreManagerResponse] = jsonFormat4(StoreManagerResponse.apply)
}

object StoreManagerResponse {
  def from(store: Store): StoreManagerResponse =
    StoreManagerResponse(
      store.manager.name,
      store.manager.nameKana,
      store.manager.email,
      store.manager.subEmail)
}
