package utils

object StringUtil {
  def hasValue(text: Option[String]): Boolean = text.isDefined && !text.get.isEmpty
}
