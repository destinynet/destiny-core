/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:28:28
 */
package destiny.core.calendar.decorators

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.io.Serializable
import java.util.*


object DayDecorator {

  private val implMap = mapOf<Locale , Decorator<Int>>(
    Locale.CHINESE to DayDecoratorChinese(),
    Locale.ENGLISH to DayDecoratorEnglish()
  )

  fun getOutputString(value: Int, locale: Locale): String {
    return implMap.getOutputString(value , locale)
  }
}


class DayDecoratorChinese : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "日"
  }
}

class DayDecoratorEnglish : Decorator<Int>, Serializable {

  override fun getOutputString(value: Int): String {
    return value.toString()
  }

}
