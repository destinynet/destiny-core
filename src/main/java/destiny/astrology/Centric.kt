/**
 * @author smallufo
 * Created on 2007/12/10 at 上午 10:30:01
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

fun Centric.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.astrology.Astrology"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Centric.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/** 中心系統  */
enum class Centric(val nameKey: String) {
  /** 地心  */
  GEO("Centric.GEO"),
  /** 日心  */
  HELIO("Centric.HELIO"),
  /** 地表  */
  TOPO("Centric.TOPO"),
  /** 質心  */
  BARY("Centric.BARY");
}
