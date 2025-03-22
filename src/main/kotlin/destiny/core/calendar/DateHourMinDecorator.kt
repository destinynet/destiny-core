/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.HOUR_OF_DAY
import java.time.temporal.ChronoField.MINUTE_OF_HOUR
import java.util.*


/** 只輸出到「分」  */
object DateHourMinDecorator {

  private val implMap = mapOf<Locale, Decorator<ChronoLocalDateTime<*>>>(
    Locale.TAIWAN to DateHourMinDecoratorTradChinese,
    Locale.CHINA to DateHourMinDecoratorSimpChinese,
    Locale.ENGLISH to DateHourMinDecoratorEnglish,
    Locale.JAPAN to DateHourMinDecoratorJapanese
  )

  fun getOutputString(time: ChronoLocalDateTime<*>, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocaleOrFirst(locale, implMap.keys)
    return implMap.getValue(bestMatchingLocale).getOutputString(time)
  }
}


/**
 * 簡單的中文輸出 , 只到「分」<BR></BR>
 * <pre>
 * 西元　2000年01月01日　00時00分
 * 西元前2000年12月31日　23時59分
</pre> *
 */
object DateHourMinDecoratorTradChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {
  private fun readResolve(): Any = DateHourMinDecoratorTradChinese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.TRADITIONAL_CHINESE))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR) , Locale.TRADITIONAL_CHINESE))
    }
  }
}


object DateHourMinDecoratorSimpChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {
  private fun readResolve(): Any = DateHourMinDecoratorSimpChinese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.SIMPLIFIED_CHINESE))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Locale.SIMPLIFIED_CHINESE))
    }
  }
}


object DateHourMinDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {
  private fun readResolve(): Any = DateHourMinDecoratorEnglish

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.ENGLISH))
      append(" ")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Locale.ENGLISH))
    }
  }
}


object DateHourMinDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {
  private fun readResolve(): Any = DateHourMinDecoratorJapanese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Locale.JAPAN))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Locale.TRADITIONAL_CHINESE))
    }
  }
}
