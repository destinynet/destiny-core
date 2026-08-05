/**
 * @author smallufo
 * Created on 2008/1/27 at 上午 2:19:44
 */
package destiny.core.calendar.eightwords

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang
import destiny.tools.Lang

fun HemisphereBy.asLocaleString() = object : ILocaleString {
  private val resource = EightWords::class.java.name
  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(resource, lang, this@asLocaleString.nameKey) ?: this@asLocaleString.nameKey
  }
}

fun HemisphereBy.toString(lang: Lang): String {
  return this.asLocaleString().getTitle(lang)
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun HemisphereBy.toString(locale: Locale): String = toString(locale.toLang())

/** 界定南北半球的方法 , 赤道 還是 赤緯  */
enum class HemisphereBy(val nameKey: String) {
  /** 赤道  */
  EQUATOR("HemisphereBy.EQUATOR"),
  /** 赤緯  */
  DECLINATION("HemisphereBy.DECLINATION");

}
