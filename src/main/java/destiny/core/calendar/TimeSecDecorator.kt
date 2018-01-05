/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*

/** 輸出到「秒」  */
object TimeSecDecorator {
  private val implMap = mapOf<Locale, Decorator<ChronoLocalDateTime<*>>>(
    Locale.TAIWAN to TimeSecDecoratorChinese(),
    Locale.ENGLISH to TimeSecDecoratorEnglish(),
    Locale.JAPAN to TimeSecDecoratorJapanese()
  )

  fun getOutputString(time: ChronoLocalDateTime<*>, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap[bestMatchingLocale]!!.getOutputString(time)
  }
}


/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR></BR>
 * <pre>
 * 西元　2000年01月01日　00時00分 00.00秒
 * 西元前2000年12月31日　23時59分 59.99秒
</pre> *
 */
class TimeSecDecoratorChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()

    logger.debug("time = {} , era = {}", value, value.toLocalDate().era)

    val min = TimeMinDecoratorChinese()
    sb.append(min.getOutputString(value))

    sb.append(' ')
    if (value.get(ChronoField.SECOND_OF_MINUTE) < 10) {
      sb.append("0")
    }
    sb.append(value.get(ChronoField.SECOND_OF_MINUTE))

    if (value.get(ChronoField.NANO_OF_SECOND) == 0) {
      sb.append(".00")
    } else {
      sb.append(".")
      sb.append(value.get(ChronoField.NANO_OF_SECOND).toString().substring(0, 2))
    }
    sb.append("秒")
    return sb.toString()
  }
}



class TimeSecDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()

    val min = TimeMinDecoratorEnglish()
    sb.append(min.getOutputString(value))
    sb.append(":")

    if (value.get(ChronoField.SECOND_OF_MINUTE) < 10) {
      sb.append("0")
    }
    sb.append(value.get(ChronoField.SECOND_OF_MINUTE))

    if (value.get(ChronoField.NANO_OF_SECOND) == 0) {
      sb.append(".00")
    } else {
      sb.append(".")
      sb.append(value.get(ChronoField.NANO_OF_SECOND).toString().substring(0, 2))
    }

    return sb.toString()
  }
}


class TimeSecDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    val sb = StringBuilder()
    val min = TimeMinDecoratorJapanese()
    sb.append(min.getOutputString(value))

    sb.append(' ')
    if (value.get(ChronoField.SECOND_OF_MINUTE) < 10) {
      sb.append("0")
    }
    sb.append(value.get(ChronoField.SECOND_OF_MINUTE))

    if (value.get(ChronoField.NANO_OF_SECOND) == 0) {
      sb.append(".00")
    } else {
      sb.append(".")
      sb.append(value.get(ChronoField.NANO_OF_SECOND).toString().substring(0, 2))
    }
    sb.append("秒")

    return sb.toString()
  }
}
