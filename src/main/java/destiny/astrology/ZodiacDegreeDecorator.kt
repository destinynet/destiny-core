/**
 * @author smallufo
 * Created on 2008/1/15 at 上午 12:55:13
 */
package destiny.astrology

import destiny.tools.LocaleTools
import destiny.tools.getOutputString
import java.util.*

object ZodiacDegreeDecorator {
  private val implMap = mapOf(
    Locale.TAIWAN to ZodiacDegreeDecoratorTradChinese(),
    Locale.US to ZodiacDegreeDecoratorEnglish()
  )


  fun getOutputString(degree: Double, locale: Locale): String {
    return implMap.getOutputString(degree , locale)
  }

  fun getSimpOutputString(degree: Double, locale: Locale): String {
    return implMap[LocaleTools.getBestMatchingLocale(locale , implMap.keys)?: implMap.keys.first()]!!.getSimpOutString(degree)
  }

}
