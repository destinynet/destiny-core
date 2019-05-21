/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import destiny.tools.Decorator
import destiny.tools.LocaleTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.IsoEra
import java.time.temporal.ChronoField.*
import java.util.*


/** 只輸出到「分」  */
object TimeMinDecorator {

  private val implMap = mapOf<Locale, Decorator<ChronoLocalDateTime<*>>>(
    Locale.TAIWAN to TimeMinDecoratorChinese(),
    Locale.CHINA to TimeMinDecoratorChina(),
    Locale.ENGLISH to TimeMinDecoratorEnglish(),
    Locale.JAPAN to TimeMinDecoratorJapanese()
  )

  fun getOutputString(time: ChronoLocalDateTime<*>, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap[bestMatchingLocale]!!.getOutputString(time)
  }
}


/**
 * 簡單的中文輸出 , 只到「分」<BR></BR>
 * <pre>
 * 西元　2000年01月01日　00時00分 00.00秒
 * 西元前2000年12月31日　23時59分 59.99秒
</pre> *
 */
class TimeMinDecoratorChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return StringBuilder().apply {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.TRADITIONAL_CHINESE))
      append("　")
      append(if (value.get(HOUR_OF_DAY) < 10) "0" else "").append(value.get(HOUR_OF_DAY)).append("時")
      append(if (value.get(MINUTE_OF_HOUR) < 10) "0" else "").append(value.get(MINUTE_OF_HOUR)).append("分")
    }.toString()
  }
}


class TimeMinDecoratorChina : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return StringBuilder().apply {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.SIMPLIFIED_CHINESE))
      append("　")
      append(if (value.get(HOUR_OF_DAY) < 10) "0" else "").append(value.get(HOUR_OF_DAY)).append("时")
      append(if (value.get(MINUTE_OF_HOUR) < 10) "0" else "").append(value.get(MINUTE_OF_HOUR)).append("分")
    }.toString()
  }
}


class TimeMinDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return StringBuilder().apply {
      append(DateDecorator.getOutputString(value.toLocalDate() , Locale.ENGLISH))
      append(" ")
      append(if (value.get(HOUR_OF_DAY) < 10) "0" else "").append(value.get(HOUR_OF_DAY))
      append(":")
      append(if (value.get(MINUTE_OF_HOUR) < 10) "0" else "").append(value.get(MINUTE_OF_HOUR))
    }.toString()
  }
}


class TimeMinDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return StringBuilder().apply {
      append("西暦")
      if (value.toLocalDate().era === IsoEra.BCE)
        append("前")
      else
        append("　")
      append(AlignTools.alignRight(value.get(YEAR_OF_ERA), 4)).append("年")
      append(if (value.get(MONTH_OF_YEAR) < 10) "0" else "").append(value.get(MONTH_OF_YEAR)).append("月")
      append(if (value.get(DAY_OF_MONTH) < 10) "0" else "").append(value.get(DAY_OF_MONTH)).append("日")
      append("　")
      append(if (value.get(HOUR_OF_DAY) < 10) "0" else "").append(value.get(HOUR_OF_DAY)).append("時")
      append(if (value.get(MINUTE_OF_HOUR) < 10) "0" else "").append(value.get(MINUTE_OF_HOUR)).append("分")
    }.toString()
  }
}
