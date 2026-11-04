/**
 * @author smallufo
 * Created on 2008/1/27 at 上午 2:19:44
 */
package destiny.core.calendar.eightwords

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

/** 界定南北半球的方法 , 赤道 還是 赤緯  */
enum class HemisphereBy(val nameKey: String) : ILocaleString {
  /** 赤道  */
  EQUATOR("HemisphereBy.EQUATOR"),
  /** 赤緯  */
  DECLINATION("HemisphereBy.DECLINATION");

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(EightWords::class.qualifiedName!!, lang, nameKey) ?: nameKey
  }
}
