/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:27:04
 */
package destiny.astrology

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*

/** 將地平方位角的「度數」，轉化為亦讀的輸出，例如：北偏東10度  */
object AzimuthDegreeDecorator {

  private val implMap = mapOf<Locale, Decorator<Double>>(
    Locale.CHINESE to AzimuthDegreeTaiwanDecorator(),
    Locale.ENGLISH to AzimuthDegreeEnglishDecorator()
  )

  fun getOutputString(value: Double, locale: Locale): String {
    return implMap.getOutputString(value , locale)
  }

}
