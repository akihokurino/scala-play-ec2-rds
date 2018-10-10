package responses

import models.Recruitment
import spray.json._

case class PaymentTypeResponse(id: Int, name: String)

object PaymentTypeProtocol extends DefaultJsonProtocol {
  implicit def paymentTypeFormat: RootJsonFormat[PaymentTypeResponse] = jsonFormat2(PaymentTypeResponse.apply)
}

object PaymentTypeResponse {
  def from(paymentType: Recruitment.PaymentType): PaymentTypeResponse = PaymentTypeResponse(paymentType.id, paymentType.text)
}