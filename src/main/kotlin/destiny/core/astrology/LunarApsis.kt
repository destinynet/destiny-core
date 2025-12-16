/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:30:01
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.core.astrology.Apsis.APHELION
import destiny.core.astrology.Apsis.PERIHELION
import destiny.tools.serializers.astrology.LunarApsisSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass


enum class MeanOscu {
  MEAN, OSCU
}


@Serializable(with = LunarApsisSerializer::class)
sealed class LunarApsis(nameKey: String, abbrKey: String,
                        /** 只會用到 PERIHELION , APHELION  */
                        val apsis: Apsis
) : LunarPoint(nameKey, abbrKey, Star::class.qualifiedName!!), Comparable<LunarApsis> {

  /**
   * 遠地點 , 月孛 , 水之餘 , Black Moon , Lilith，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.APHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.APOGEE_MEAN, gmt);
   * </pre>
   */
  object APOGEE : LunarApsis("LunarApsis.APOGEE", "LunarApsis.APOGEE_ABBR", APHELION)

  /**
   * 近地點，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.PERIHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.PERIGEE_MEAN , gmt);
   * </pre>
   */
  object PERIGEE : LunarApsis("LunarApsis.PERIGEE", "LunarApsis.PERIGEE_ABBR", PERIHELION)

  override fun compareTo(other: LunarApsis): Int {
    if (this == other)
      return 0
    return values.indexOf(this) - values.indexOf(other)
  }

  companion object : IPoints<LunarApsis> {

    override val type: KClass<out Point> = LunarApsis::class

    override val values by lazy { arrayOf(APOGEE, PERIGEE) }

    override fun fromString(value: String, locale: Locale) : LunarApsis? {
      return values.firstOrNull { it::class.simpleName == value }
    }
  }
}
