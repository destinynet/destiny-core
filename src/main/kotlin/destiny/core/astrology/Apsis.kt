/**
 * @author smallufo
 * Created on 2007/5/27 at 上午 2:18:45
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

/**
 * 橢圓軌道的四個點：近點（Perihelion/Perigee）、遠點（Aphelion/Apogee），上升點（Ascending/North Node），下降點（Descending/South Node）
 */
enum class Apsis(val nameKey: String) : ILocaleString {
  /** 近點 , perigee */
  PERIHELION("Apsis.PERIHELION"),
  /** 遠點 , apogee */
  APHELION("Apsis.APHELION"),
  /** 北交點/上升點  */
  ASCENDING("Apsis.ASCENDING"),
  /** 南交點/下降點  */
  DESCENDING("Apsis.DESCENDING");

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string("destiny.core.astrology.Star", lang, nameKey) ?: nameKey
  }
}
