/**
 * @author smallufo
 * Created on 2008/4/2 at 上午 1:26:30
 */
package destiny.core

import destiny.tools.LocaleTools
import java.util.*

object GenderDecorator {
  private val implMap = mapOf(
    Locale.TAIWAN to GenderDecoratorChinese(),
    Locale.ENGLISH to GenderDecoratorEnglish()
  )

  fun getOutputString(gender: Gender, locale: Locale): String {
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap[bestMatchingLocale]!!.getOutputString(gender)
  }
}
