/**
 * Created by smallufo on 2018-03-25.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDate
import java.time.chrono.IsoEra
import java.time.temporal.ChronoField.*
import java.util.*

object DateDecorator {

  private val implMap = mapOf<Locale, Decorator<ChronoLocalDate>>(
    Locale.TAIWAN to DateDecoratorChinese(),
    Locale.SIMPLIFIED_CHINESE to DateDecoratorChinese(),
    Locale.ENGLISH to DateDecoratorEnglish()
                                                                 )

  fun getOutputString(date: ChronoLocalDate, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap.getValue(bestMatchingLocale).getOutputString(date)
  }
}

/**
 * 簡單的中文輸出 , 只到「日期」<BR></BR>
 * 西元　2000年01月01日
 * 西元前2000年12月31日
 */
class DateDecoratorChinese : Decorator<ChronoLocalDate>, Serializable {
  override fun getOutputString(value: ChronoLocalDate): String {

    return with(StringBuilder()) {
      append("西元")
      if (value.era === IsoEra.BCE || value.era === org.threeten.extra.chrono.JulianEra.BC) {
        append("前")
      } else
        append("　")

      append(AlignTools.alignRight(value.get(YEAR_OF_ERA), 4)).append("年")
      append(if (value.get(MONTH_OF_YEAR) < 10) "0" else "").append(value.get(MONTH_OF_YEAR)).append("月")
      append(if (value.get(DAY_OF_MONTH) < 10) "0" else "").append(value.get(DAY_OF_MONTH)).append("日")
    }.toString()
  }
}

class DateDecoratorEnglish : Decorator<ChronoLocalDate>, Serializable {
  override fun getOutputString(value: ChronoLocalDate): String {
    return with(StringBuilder()) {
      append(value.get(YEAR_OF_ERA))
      if (value.era === IsoEra.CE)
        append("AD")
      else
        append("BC")
      append(" ")

      append(if (value.get(MONTH_OF_YEAR) < 10) "0" else "").append(value.get(MONTH_OF_YEAR))
      append("/")
      append(if (value.get(DAY_OF_MONTH) < 10) "0" else "").append(value.get(DAY_OF_MONTH))
    }.toString()
  }

}