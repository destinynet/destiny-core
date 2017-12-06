/**
 * @author smallufo
 * Created on 2008/1/15 at 上午 12:55:13
 */
package destiny.astrology

import destiny.tools.LocaleUtils
import java.util.*

object ZodiacDegreeDecorator {
  private val implMap = mapOf(
    Locale.TAIWAN to ZodiacDegreeDecoratorTradChinese(),
    Locale.US to ZodiacDegreeDecoratorEnglish()
  )


  fun getOutputString(degree: Double, locale: Locale): String {
    return implMap[LocaleUtils.getBestMatchingLocale(locale, implMap.keys).orElse(implMap.keys.toTypedArray()[0] as Locale)]!!.getOutputString(degree)
  }

  fun getSimpOutputString(degree: Double, locale: Locale): String {
    return implMap[LocaleUtils.getBestMatchingLocale(locale, implMap.keys).orElse(implMap.keys.toTypedArray()[0] as Locale)]!!.getSimpOutString(degree)
  }

}
