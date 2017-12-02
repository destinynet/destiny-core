/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.IsoEra
import java.time.chrono.IsoEra.BCE
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

  private val logger = LoggerFactory.getLogger(javaClass)


  override fun getOutputString(time: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()

    logger.debug("time = {} , era = {}", time, time.toLocalDate().era)

    sb.append("西元")
    val localDate = time.toLocalDate()
    if (localDate.era === BCE || localDate.era === org.threeten.extra.chrono.JulianEra.BC) {
      sb.append("前")
    } else
      sb.append("　")
    sb.append(TimeMinDecoratorChinese.alignRight(time.get(YEAR_OF_ERA), 4)).append("年")
    sb.append(if (time.get(MONTH_OF_YEAR) < 10) "0" else "").append(time.get(MONTH_OF_YEAR)).append("月")
    sb.append(if (time.get(DAY_OF_MONTH) < 10) "0" else "").append(time.get(DAY_OF_MONTH)).append("日")
    sb.append("　")
    sb.append(if (time.get(HOUR_OF_DAY) < 10) "0" else "").append(time.get(HOUR_OF_DAY)).append("時")
    sb.append(if (time.get(MINUTE_OF_HOUR) < 10) "0" else "").append(time.get(MINUTE_OF_HOUR)).append("分")

    return sb.toString()
  }

  companion object {

    fun alignRight(value: Int, width: Int): String {
      val sb = StringBuffer(value.toString())
      val valueLength = sb.length

      return destiny.tools.ColorCanvas.AlignUtil.outputStringBuffer(valueLength, width, sb)
    }
  }
}


class TimeMinDecoratorChina : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(time: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()

    sb.append("西元")
    if (time.toLocalDate().era === BCE) {
      sb.append("前")
    } else
      sb.append("　")
    sb.append(TimeMinDecoratorChinese.alignRight(time.get(YEAR_OF_ERA), 4)).append("年")
    sb.append(if (time.get(MONTH_OF_YEAR) < 10) "0" else "").append(time.get(MONTH_OF_YEAR)).append("月")
    sb.append(if (time.get(DAY_OF_MONTH) < 10) "0" else "").append(time.get(DAY_OF_MONTH)).append("日")
    sb.append("　")
    sb.append(if (time.get(HOUR_OF_DAY) < 10) "0" else "").append(time.get(HOUR_OF_DAY)).append("时")
    sb.append(if (time.get(MINUTE_OF_HOUR) < 10) "0" else "").append(time.get(MINUTE_OF_HOUR)).append("分")

    return sb.toString()
  }
}


class TimeMinDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(time: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()
    sb.append(time.get(YEAR_OF_ERA))
    if (time.toLocalDate().era === IsoEra.CE)
      sb.append("AD")
    else
      sb.append("BC")
    sb.append(" ")

    sb.append(if (time.get(MONTH_OF_YEAR) < 10) "0" else "").append(time.get(MONTH_OF_YEAR))
    sb.append("/")
    sb.append(if (time.get(DAY_OF_MONTH) < 10) "0" else "").append(time.get(DAY_OF_MONTH))
    sb.append(" ")

    sb.append(if (time.get(HOUR_OF_DAY) < 10) "0" else "").append(time.get(HOUR_OF_DAY))
    sb.append(":")
    sb.append(if (time.get(MINUTE_OF_HOUR) < 10) "0" else "").append(time.get(MINUTE_OF_HOUR))
    return sb.toString()
  }
}


class TimeMinDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(time: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()
    sb.append("西暦")
    if (time.toLocalDate().era === IsoEra.BCE)
      sb.append("前")
    else
      sb.append("　")
    sb.append(TimeMinDecoratorChinese.alignRight(time.get(YEAR_OF_ERA), 4)).append("年")
    sb.append(if (time.get(MONTH_OF_YEAR) < 10) "0" else "").append(time.get(MONTH_OF_YEAR)).append("月")
    sb.append(if (time.get(DAY_OF_MONTH) < 10) "0" else "").append(time.get(DAY_OF_MONTH)).append("日")
    sb.append("　")
    sb.append(if (time.get(HOUR_OF_DAY) < 10) "0" else "").append(time.get(HOUR_OF_DAY)).append("時")
    sb.append(if (time.get(MINUTE_OF_HOUR) < 10) "0" else "").append(time.get(MINUTE_OF_HOUR)).append("分")
    return sb.toString()
  }
}
