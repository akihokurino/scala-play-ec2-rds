package models

case class Entry(id: Int,
                 from: User,
                 to: Store,
                 question: Option[Question],
                 occupations: List[Occupation])

object Entry {

}
