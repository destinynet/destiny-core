/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.LocaleTools
import destiny.tools.JSerializable
import java.time.chrono.ChronoLocalDateTime

/** 輸出到「秒」  */
object DateHourMinSecDecorator : LocalizedDecorator<ChronoLocalDateTime<*>> {
  private val implMap = mapOf<Lang, Decorator<ChronoLocalDateTime<*>>>(
    Lang.ZH_TW to DateHourMinSecDecoratorTradChinese,
    Lang.EN to DateHourMinSecDecoratorEnglish,
    Lang.JA_JP to DateHourMinSecDecoratorJapanese
  )

  override fun getOutputString(time: ChronoLocalDateTime<*>, lang: Lang): String {
    val bestMatchingLocale = LangTools.getBestMatchingLangOrFirst(lang, implMap.keys)
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
object DateHourMinSecDecoratorTradChinese : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinSecDecoratorTradChinese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Lang.ZH_TW))
      append('　')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime() , Lang.ZH_TW))
    }
  }
}


object DateHourMinSecDecoratorJapanese : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinSecDecoratorJapanese

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {

    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Lang.JA_JP))
      append('　')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime(), Lang.ZH_TW))
    }
  }
}



object DateHourMinSecDecoratorEnglish : Decorator<ChronoLocalDateTime<*>>, JSerializable {
  private fun readResolve(): Any = DateHourMinSecDecoratorEnglish

  override fun getOutputString(value: ChronoLocalDateTime<*>): String {
    return buildString {
      append(DateDecorator.getOutputString(value.toLocalDate(), Lang.EN))
      append(' ')
      append(HourMinSecDecorator.getOutputString(value.toLocalTime() , Lang.EN))
    }
  }
}

