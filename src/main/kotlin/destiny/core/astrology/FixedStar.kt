/**
 * @author smallufo
 * Created on 2007/12/20 at 上午 12:16:32
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.core.toString
import destiny.tools.serializers.astrology.FixedStarSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/** 恆星  */
@Serializable(with = FixedStarSerializer::class)
sealed class FixedStar(nameKey: String, abbrKey: String) : Star(nameKey, abbrKey, Star::class.qualifiedName!!), Comparable<FixedStar> {
  /** Algol 大陵五  */
  object ALGOL : FixedStar("FixedStar.ALGOL", "FixedStar.ALGOL_ABBR")

  /** Aldebaran 畢宿五  */
  object ALDEBARAN : FixedStar("FixedStar.ALDEBARAN", "FixedStar.ALDEBARAN_ABBR")

  /** Rigel 參宿七  */
  object RIGEL : FixedStar("FixedStar.RIGEL", "FixedStar.RIGEL_ABBR")

  /** Capella 五車二  */
  object CAPELLA : FixedStar("FixedStar.CAPELLA", "FixedStar.CAPELLA_ABBR")

  /** Betelgeuse 參宿四  */
  object BETELGEUSE : FixedStar("FixedStar.BETELGEUSE", "FixedStar.BETELGEUSE_ABBR")

  /** Sirius 天狼星  */
  object SIRIUS : FixedStar("FixedStar.SIRIUS", "FixedStar.SIRIUS_ABBR")

  /** Canopus 老人星  */
  object CANOPUS : FixedStar("FixedStar.CANOPUS", "FixedStar.CANOPUS_ABBR")

  /** Pollux 北河三  */
  object POLLUX : FixedStar("FixedStar.POLLUX", "FixedStar.POLLUX_ABBR")

  /** Procyon 南河三  */
  object PROCYON : FixedStar("FixedStar.PROCYON", "FixedStar.PROCYON_ABBR")

  /** Praesepe 鬼宿  */
  object PRAESEPE : FixedStar("FixedStar.PRAESEPE", "FixedStar.PRAESEPE_ABBR")

  /** Alphard 星宿一  */
  object ALPHARD : FixedStar("FixedStar.ALPHARD", "FixedStar.ALPHARD_ABBR")

  /** Regulus 軒轅十四  */
  object REGULUS : FixedStar("FixedStar.REGULUS", "FixedStar.REGULUS_ABBR")

  /** Spica 角宿一  */
  object SPICA : FixedStar("FixedStar.SPICA", "FixedStar.SPICA_ABBR")

  /** Arcturus 大角  */
  object ARCTURUS : FixedStar("FixedStar.ARCTURUS", "FixedStar.ARCTURUS_ABBR")

  /** Antares 心宿二  */
  object ANTARES : FixedStar("FixedStar.ANTARES", "FixedStar.ANTARES_ABBR")

  /** Vega 織女星  */
  object VEGA : FixedStar("FixedStar.VEGA", "FixedStar.VEGA_ABBR")

  /** Altair 牛郎星  */
  object ALTAIR : FixedStar("FixedStar.ALTAIR", "FixedStar.ALTAIR_ABBR")

  /** Fomalhaut 北落師門  */
  object FOMALHAUT : FixedStar("FixedStar.FOMALHAUT", "FixedStar.FOMALHAUT_ABBR")

  /** Deneb 天津四  */
  object DENEB : FixedStar("FixedStar.DENEB", "FixedStar.DENEB_ABBR")

  override fun compareTo(other: FixedStar): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }


  companion object : IPoints<FixedStar> {

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

    override fun fromString(value: String, locale: Locale): FixedStar? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }
  }
}
