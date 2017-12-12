/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:56:28
 */
package destiny.core.calendar.decorators

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.io.Serializable
import java.util.*

object MinuteDecorator {
  private val implMap = mapOf<Locale, Decorator<Int>>(
    Locale.CHINESE to MinuteDecoratorChinese(),
    Locale.ENGLISH to MinuteDecoratorEnglish()
  )

  fun getOutputString(value: Int, locale: Locale): String {
    return implMap.getOutputString(value , locale)
  }
}

class MinuteDecoratorChinese : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "分"
  }
}


class MinuteDecoratorEnglish : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }
}
