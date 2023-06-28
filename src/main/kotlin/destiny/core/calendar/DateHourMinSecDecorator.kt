/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 輸出到「秒」  */
object DateHourMinSecDecorator {
  private val implMap = mapOf<Locale, Decorator<ChronoLocalDateTime<*>>>(
    Locale.TAIWAN to DateHourMinSecDecoratorTradChinese,
    Locale.ENGLISH to DateHourMinSecDecoratorEnglish,
    Locale.JAPAN to DateHourMinSecDecoratorJapanese()
  )

  fun getOutputString(time: ChronoLocalDateTime<*>, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap.getValue(bestMatchingLocale).getOutputString(time)
  }
}


/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR></BR>
 * <pre>
 * 西元　2000年01月01日　00時00分 00.00秒
 * 西元前2000年12月31日　23時59分 59.99秒
</pre> *
 */
object DateHourMinSecDecoratorTradChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Locale.TAIWAN))
      append('　')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime() , Locale.TAIWAN))
    }
  }
}


class DateHourMinSecDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Locale.JAPAN))
      append('　')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime(), Locale.TAIWAN))
    }
  }
}



object DateHourMinSecDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Locale.ENGLISH))
      append(' ')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime() , Locale.ENGLISH))
    }
  }
}

