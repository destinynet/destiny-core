/**
 * @author smallufo
 * Created on 2008/1/27 at 上午 2:19:44
 */
package destiny.core.calendar.eightwords

import destiny.tools.ILocaleString
import java.util.*

fun HemisphereBy.asLocaleString() = object : ILocaleString {
  private val resource = EightWords::class.java.name
  override fun getTitle(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun HemisphereBy.toString(locale: Locale): String {
  return this.asLocaleString().getTitle(locale)
}

/** 界定南北半球的方法 , 赤道 還是 赤緯  */
enum class HemisphereBy(val nameKey: String) {
  /** 赤道  */
  EQUATOR("HemisphereBy.EQUATOR"),
  /** 赤緯  */
  DECLINATION("HemisphereBy.DECLINATION");

}
