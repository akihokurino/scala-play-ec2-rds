package models

case class Prefecture(id: Int, name: String, areas: List[Area])

object Prefecture {
  private val hasAreaIds = Array(11, 12, 13, 14, 23, 27)

  def hasArea(prefectureId: Int): Boolean = hasAreaIds.contains(prefectureId)
}
