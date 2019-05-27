/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
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
 * 中文、日文 共同 share 此 function
 */
private val chineseJapaneseCommonFunction = { sb: StringBuilder, value : ChronoLocalDateTime<*> ->
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
}

/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR></BR>
 * <pre>
 * 西元　2000年01月01日　00時00分 00.00秒
 * 西元前2000年12月31日　23時59分 59.99秒
</pre> *
 */
class TimeSecDecoratorChinese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  private val minDecorator = TimeMinDecoratorChinese()

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return StringBuilder().apply {
      append(minDecorator.getOutputString(value))
      chineseJapaneseCommonFunction.invoke(this , value)
    }.toString()
  }
}


class TimeSecDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, Serializable {

  private val minDecorator = TimeMinDecoratorJapanese()

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return StringBuilder().apply {
      append(minDecorator.getOutputString(value))
      chineseJapaneseCommonFunction.invoke(this , value)
    }.toString()
  }
}



class TimeSecDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, Serializable {

  private val minDecorator = TimeMinDecoratorEnglish()

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return StringBuilder().apply {
      append(minDecorator.getOutputString(value))
      append(":")

      if (value.get(ChronoField.SECOND_OF_MINUTE) < 10) {
        append("0")
      }
      append(value.get(ChronoField.SECOND_OF_MINUTE))

      if (value.get(ChronoField.NANO_OF_SECOND) == 0) {
        append(".00")
      } else {
        append(".")
        append(value.get(ChronoField.NANO_OF_SECOND).toString().substring(0, 2))
      }
    }.toString()
  }
}

