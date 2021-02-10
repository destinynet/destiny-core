/**
 * @author smallufo
 * Created on 2007/5/27 at 上午 2:18:45
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import java.util.*

fun Apsis.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.core.astrology.Star"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun Apsis.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

/**
 * 橢圓軌道的四個點：近點（Perihelion/Perigee）、遠點（Aphelion/Apogee），上升點（Ascending/North Node），下降點（Descending/South Node）
 */
enum class Apsis(val nameKey: String) {
  /** 近點  */
  PERIHELION("Apsis.PERIHELION"),
  /** 遠點  */
  APHELION("Apsis.APHELION"),
  /** 北交點/上升點  */
  ASCENDING("Apsis.ASCENDING"),
  /** 南交點/下降點  */
  DESCENDING("Apsis.DESCENDING");
}
