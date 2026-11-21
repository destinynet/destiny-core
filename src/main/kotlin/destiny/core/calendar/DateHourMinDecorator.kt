/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.LocaleTools
import destiny.tools.JSerializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.HOUR_OF_DAY
import java.time.temporal.ChronoField.MINUTE_OF_HOUR


/** 只輸出到「分」  */
object DateHourMinDecorator : LocalizedDecorator<ChronoLocalDateTime<*>> {

  private val implMap = mapOf<Lang, Decorator<ChronoLocalDateTime<*>>>(
    Lang.ZH_TW to DateHourMinDecoratorTradChinese,
    Lang.ZH_CN to DateHourMinDecoratorSimpChinese,
    Lang.EN to DateHourMinDecoratorEnglish,
    Lang.JA_JP to DateHourMinDecoratorJapanese
  )

  override fun getOutputString(time: ChronoLocalDateTime<*>, lang: Lang): String {
    val bestMatchingLocale = LangTools.getBestMatchingLangOrFirst(lang, implMap.keys)
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
object DateHourMinDecoratorTradChinese : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinDecoratorTradChinese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Lang.ZH_TW))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR) , Lang.ZH_TW))
    }
  }
}


object DateHourMinDecoratorSimpChinese : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinDecoratorSimpChinese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Lang.ZH_CN))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Lang.ZH_CN))
    }
  }
}


object DateHourMinDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinDecoratorEnglish

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate() , Lang.EN))
      append(" ")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Lang.EN))
    }
  }
}


object DateHourMinDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinDecoratorJapanese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Lang.JA_JP))
      append("　")
      append(HourMinDecorator.getOutputString(value.get(HOUR_OF_DAY) to value.get(MINUTE_OF_HOUR), Lang.ZH_TW))
    }
  }
}
