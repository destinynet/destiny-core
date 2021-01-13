/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:15:48
 */
package destiny.core.calendar.decorators

import destiny.tools.ChineseStringTools
import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.io.Serializable
import java.util.*

object MonthDecorator {
  private val implMap = mapOf<Locale, Decorator<Int>>(
    Locale.CHINESE to MonthDecoratorChinese(),
    Locale.ENGLISH to MonthDecoratorEnglish()
  )

  fun getOutputString(value: Int, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}


class MonthDecoratorChinese : Decorator<Int>, Serializable {

  override fun getOutputString(value: Int): String {
    return when (value) {
      in (1..9) -> ChineseStringTools.digitToChinese(value)+"月"
      10 -> "十月"
      11 -> "十一月"
      12 -> "十二月"
      else -> throw IllegalArgumentException("impossible")
    }
  }
}


class MonthDecoratorEnglish : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return when (value) {
      1 -> "January"
      2 -> "February"
      3 -> "March"
      4 -> "April"
      5 -> "May"
      6 -> "June"
      7 -> "July"
      8 -> "August"
      9 -> "September"
      10 -> "October"
      11 -> "November"
      12 -> "December"
      else -> throw IllegalArgumentException("impossible")
    }
  }

}
