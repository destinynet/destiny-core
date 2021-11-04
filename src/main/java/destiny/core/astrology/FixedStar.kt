/**
 * @author smallufo
 * Created on 2007/12/20 at 上午 12:16:32
 */
package destiny.core.astrology

import java.util.*
import kotlin.reflect.KClass

/** 恆星  */
sealed class FixedStar(nameKey: String, abbrKey: String) : Star(nameKey, abbrKey, Star::class.qualifiedName!!), Comparable<FixedStar> {
  /** Algol 大陵五  */
  object ALGOL : FixedStar("Fixed.ALGOL", "Fixed.ALGOL_ABBR")

  /** Aldebaran 畢宿五  */
  object ALDEBARAN : FixedStar("Fixed.ALDEBARAN", "Fixed.ALDEBARAN_ABBR")

  /** Rigel 參宿七  */
  object RIGEL : FixedStar("Fixed.RIGEL", "Fixed.RIGEL_ABBR")

  /** Capella 五車二  */
  object CAPELLA : FixedStar("Fixed.CAPELLA", "Fixed.CAPELLA_ABBR")

  /** Betelgeuse 參宿四  */
  object BETELGEUSE : FixedStar("Fixed.BETELGEUSE", "Fixed.BETELGEUSE_ABBR")

  /** Sirius 天狼星  */
  object SIRIUS : FixedStar("Fixed.SIRIUS", "Fixed.SIRIUS_ABBR")

  /** Canopus 老人星  */
  object CANOPUS : FixedStar("Fixed.CANOPUS", "Fixed.CANOPUS_ABBR")

  /** Pollux 北河三  */
  object POLLUX : FixedStar("Fixed.POLLUX", "Fixed.POLLUX_ABBR")

  /** Procyon 南河三  */
  object PROCYON : FixedStar("Fixed.PROCYON", "Fixed.PROCYON_ABBR")

  /** Praesepe 鬼宿  */
  object PRAESEPE : FixedStar("Fixed.PRAESEPE", "Fixed.PRAESEPE_ABBR")

  /** Alphard 星宿一  */
  object ALPHARD : FixedStar("Fixed.ALPHARD", "Fixed.ALPHARD_ABBR")

  /** Regulus 軒轅十四  */
  object REGULUS : FixedStar("Fixed.REGULUS", "Fixed.REGULUS_ABBR")

  /** Spica 角宿一  */
  object SPICA : FixedStar("Fixed.SPICA", "Fixed.SPICA_ABBR")

  /** Arcturus 大角  */
  object ARCTURUS : FixedStar("Fixed.ARCTURUS", "Fixed.ARCTURUS_ABBR")

  /** Antares 心宿二  */
  object ANTARES : FixedStar("Fixed.ANTARES", "Fixed.ANTARES_ABBR")

  /** Vega 織女星  */
  object VEGA : FixedStar("Fixed.VEGA", "Fixed.VEGA_ABBR")

  /** Altair 牛郎星  */
  object ALTAIR : FixedStar("Fixed.ALTAIR", "Fixed.ALTAIR_ABBR")

  /** Fomalhaut 北落師門  */
  object FOMALHAUT : FixedStar("Fixed.FOMALHAUT", "Fixed.FOMALHAUT_ABBR")

  /** Deneb 天津四  */
  object DENEB : FixedStar("Fixed.DENEB", "Fixed.DENEB_ABBR")

  override fun compareTo(other: FixedStar): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }


  companion object : IPoint<FixedStar> {

    override val type: KClass<out Point> = FixedStar::class

    override val values by lazy {
      arrayOf(
        REGULUS, SPICA, ALGOL, ALDEBARAN, RIGEL,
        CAPELLA, BETELGEUSE, SIRIUS, CANOPUS, POLLUX,
        PROCYON, PRAESEPE, ALPHARD, ARCTURUS, ANTARES,
        VEGA, ALTAIR, FOMALHAUT, DENEB
      )
    }
    val list by lazy { listOf(*values) }

    override fun fromString(value: String): FixedStar? {
      return values.firstOrNull {
        it.toString(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }
  }
}
