package responses

import models.Entry
import responses.UserProtocol._
import responses.StoreOverviewProtocol._
import responses.QuestionProtocol._
import responses.OccupationProtocol._
import spray.json._

case class EntryResponse(id: Int,
                         user: UserResponse,
                         store: StoreOverviewResponse,
                         question: Option[QuestionResponse],
                         occupations: List[OccupationResponse])

object EntryProtocol extends DefaultJsonProtocol {
  implicit def entryFormat: RootJsonFormat[EntryResponse] = jsonFormat5(EntryResponse.apply)
}

object EntryResponse {
  def from(entry: Entry): EntryResponse =
    EntryResponse(
      entry.id,
      UserResponse.from(entry.from),
      StoreOverviewResponse.from(entry.to),
      entry.question.map(QuestionResponse.from),
      entry.occupations.map(OccupationResponse.from)
    )
}




