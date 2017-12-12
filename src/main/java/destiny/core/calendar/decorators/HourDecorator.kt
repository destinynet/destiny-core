/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:49:59
 */
package destiny.core.calendar.decorators

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.io.Serializable
import java.util.*

object HourDecorator {
  private val implMap = mapOf<Locale, Decorator<Int>>(
    Locale.TAIWAN to HourDecoratorTaiwan(),
    Locale.SIMPLIFIED_CHINESE to HourDecoratorChina(),
    Locale.ENGLISH to HourDecoratorEnglish()
  )

  fun getOutputString(value: Int, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}


class HourDecoratorTaiwan : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "時"
  }
}


class HourDecoratorChina : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "时"
  }
}

class HourDecoratorEnglish : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }

}
