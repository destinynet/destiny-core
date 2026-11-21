/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.JSerializable
import destiny.tools.LocaleTools
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object HourMinSecDecorator : LocalizedDecorator<LocalTime> {
  private val implMap = mapOf<Lang, Decorator<LocalTime>>(
    Lang.ZH_TW to LocalTimeDecoratorTradChinese,
    Lang.ZH_CN to LocalTimeDecoratorSimpChinese,
    Lang.EN to LocalTimeDecoratorEnglish,
  )

  override fun getOutputString(localTime: LocalTime, lang: Lang): String {
    val bestMatchingLocale = LangTools.getBestMatchingLangOrFirst(lang, implMap.keys)
    return implMap.getValue(bestMatchingLocale).getOutputString(localTime)
  }
}

val secNanoPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("ss.SS")

object LocalTimeDecoratorTradChinese : Decorator<LocalTime>, JSerializable {
  override fun getOutputString(value: LocalTime): String {

    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Lang.ZH_TW))
      append(' ')
      append(value.format(secNanoPattern))
      append("秒")
    }
  }
}

object LocalTimeDecoratorSimpChinese : Decorator<LocalTime>, JSerializable {
  override fun getOutputString(value: LocalTime): String {
    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Lang.ZH_CN))
      append(' ')
      append(value.format(secNanoPattern))
      append("秒")
    }
  }
}


object LocalTimeDecoratorEnglish : Decorator<LocalTime>, JSerializable {
  override fun getOutputString(value: LocalTime): String {
    return buildString {
      append(HourMinDecorator.getOutputString(value.hour to value.minute, Lang.EN))
      append(':')
      append(value.format(secNanoPattern))
    }
  }
}
