package responses

import models.BusinessCondition
import spray.json._

case class BusinessConditionResponse(id: Int, name: String)

object BusinessConditionProtocol extends DefaultJsonProtocol {
  implicit def businessConditionFormat: RootJsonFormat[BusinessConditionResponse] = jsonFormat2(BusinessConditionResponse.apply)
}

object BusinessConditionResponse {
  def from(condition: BusinessCondition): BusinessConditionResponse = BusinessConditionResponse(condition.id, condition.name)
}
