/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 04:22:40
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang
import destiny.tools.Lang

fun TransPoint.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.core.astrology.Star"
  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(resource, lang, this@asLocaleString.nameKey) ?: this@asLocaleString.nameKey
  }
}

fun TransPoint.getTitle(locale: Locale): String {
  return this.asLocaleString().getTitle(locale)
}

/**
 * 四個角點，天底、東昇點、天頂、西落
 */
enum class TransPoint(val nameKey: String) {

  /** 東昇  */
  RISING("TransPoint.RISING"),
  /** 天頂  */
  MERIDIAN("TransPoint.MERIDIAN"),
  /** 西落  */
  SETTING("TransPoint.SETTING"),
  /** 天底  */
  NADIR("TransPoint.NADIR");
}
