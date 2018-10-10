package utils

object IntUtil {
  def hasValue(num: Option[Int]): Boolean = num.isDefined && num.get > 0
}
