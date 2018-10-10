package utils

import java.text.SimpleDateFormat
import java.util.Calendar

object DateUtil {
  def lastWeekRange(): List[String] = {
    var results: List[String] = List.empty

    val format = new SimpleDateFormat("yyyy-MM-dd")
    val cal = Calendar.getInstance
    cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 1))
    cal.add(Calendar.DAY_OF_YEAR, -1)

    val result = format.format(cal.getTime)
    results :+= result

    var i = 6
    while (i > 0) {
      cal.add(Calendar.DAY_OF_YEAR, -1)
      results :+= format.format(cal.getTime)

      i -= 1
    }

    results.reverse
  }
}
