/**
 * @author smallufo
 * Created on 2007/12/10 at 上午 10:30:01
 */
package destiny.astrology

import destiny.tools.ILocaleString
import java.util.*

/** 中心系統  */
enum class Centric(private val nameKey: String) : ILocaleString {
  /** 地心  */
  GEO("Centric.GEO"),
  /** 日心  */
  HELIO("Centric.HELIO"),
  /** 地表  */
  TOPO("Centric.TOPO"),
  /** 質心  */
  BARY("Centric.BARY");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private val resource = "destiny.astrology.Astrology"
  }

}
