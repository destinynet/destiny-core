/**
 * @author smallufo
 * Created on 2008/5/9 at 上午 5:27:04
 */
package destiny.core.astrology

import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.getOutputString

/** 將地平方位角的「度數」，轉化為亦讀的輸出，例如：北偏東10度  */
object AzimuthDegreeDecorator : LocalizedDecorator<Double> {

  private val implMap = mapOf<Lang, Decorator<Double>>(
    Lang.ZH to AzimuthDegreeTaiwanDecorator(),
    Lang.EN to AzimuthDegreeEnglishDecorator()
  )

  override fun getOutputString(value: Double, lang: Lang): String {
    return implMap.getOutputString(value , lang)
  }

}


