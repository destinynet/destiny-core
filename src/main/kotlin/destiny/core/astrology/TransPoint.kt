/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 04:22:40
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

/**
 * 四個角點，天底、東昇點、天頂、西落
 */
enum class TransPoint(val nameKey: String) : ILocaleString {

  /** 東昇  */
  RISING("TransPoint.RISING"),
  /** 天頂  */
  MERIDIAN("TransPoint.MERIDIAN"),
  /** 西落  */
  SETTING("TransPoint.SETTING"),
  /** 天底  */
  NADIR("TransPoint.NADIR");

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string("destiny.core.astrology.Star", lang, nameKey) ?: nameKey
  }
}
