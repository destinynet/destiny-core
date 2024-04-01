/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.util.*

object HourMinDecorator {
  private val implMap = mapOf<Locale, Decorator<Pair<Int, Int>>>(
    Locale.TAIWAN to HourMinDecoratorTradChinese,
    Locale.CHINA to HourMinDecoratorSimplifiedChinese,
    Locale.ENGLISH to HourMinDecoratorEnglish
  )

  fun getOutputString(hourMin: Pair<Int, Int>, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocaleOrFirst(locale, implMap.keys)
    return implMap.getValue(bestMatchingLocale).getOutputString(hourMin)
  }
}

object HourMinDecoratorTradChinese : Decorator<Pair<Int, Int>>, java.io.Serializable {
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first).append("時")
      append(if (value.second < 10) "0" else "").append(value.second).append("分")
    }
  }
}

object HourMinDecoratorSimplifiedChinese : Decorator<Pair<Int, Int>>, java.io.Serializable {
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first).append("时")
      append(if (value.second < 10) "0" else "").append(value.second).append("分")
    }
  }
}

object HourMinDecoratorEnglish : Decorator<Pair<Int, Int>>, java.io.Serializable {
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first)
      append(":")
      append(if (value.second < 10) "0" else "").append(value.second)
    }
  }
}
