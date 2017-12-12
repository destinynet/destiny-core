/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 12:31:36
 */
package destiny.core.calendar.decorators

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.io.Serializable
import java.util.*

object YearDecorator {
  private val implMap = mapOf<Locale, Decorator<Int>>(
    Locale.TAIWAN to YearDecoratorTaiwan(),
    Locale.CHINA to YearDecoratorChina(),
    Locale.ENGLISH to YearDecoratorEnglish(),
    Locale.JAPAN to YearDecoratorJapanese()
  )

  fun getOutputString(value: Int, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}


class YearDecoratorTaiwan : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return if (value - 1911 >= 1)
      "西元" + value + "年 (民國" + (value - 1911) + "年)"
    else
    //民國前
      "西元" + value + "年 (民國前" + (1912 - value) + "年)"
  }
}


class YearDecoratorChina : Decorator<Int>, Serializable {

  override fun getOutputString(value: Int): String {
    return "西元" + value.toString() + "年"
  }

}

class YearDecoratorJapanese : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return "西暦年" + value + "年"
  }
}


class YearDecoratorEnglish : Decorator<Int>, Serializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }

}
