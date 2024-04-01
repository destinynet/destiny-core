/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object HourMinSecDecorator {
  private val implMap = mapOf<Locale, Decorator<LocalTime>>(
    Locale.TAIWAN to LocalTimeDecoratorTradChinese,
    Locale.CHINA to LocalTimeDecoratorSimpChinese,
    Locale.ENGLISH to LocalTimeDecoratorEnglish,
  )

  fun getOutputString(localTime: LocalTime, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocaleOrFirst(locale, implMap.keys)
    return implMap.getValue(bestMatchingLocale).getOutputString(localTime)
  }
}

val secNanoPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("ss.SS")

object LocalTimeDecoratorTradChinese : Decorator<LocalTime>, java.io.Serializable {
  override fun getOutputString(value: LocalTime): String {

    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Locale.TAIWAN))
      append(' ')
      append(value.format(secNanoPattern))
      append("秒")
    }
  }
}

object LocalTimeDecoratorSimpChinese : Decorator<LocalTime>, java.io.Serializable {
  override fun getOutputString(value: LocalTime): String {
    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Locale.CHINA))
      append(' ')
      append(value.format(secNanoPattern))
      append("秒")
    }
  }
}


object LocalTimeDecoratorEnglish : Decorator<LocalTime>, java.io.Serializable {
  override fun getOutputString(value: LocalTime): String {
    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Locale.ENGLISH))
      append(':')
      append(value.format(secNanoPattern))
    }
  }
}
