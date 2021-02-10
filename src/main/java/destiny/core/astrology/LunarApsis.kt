/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:30:01
 */
package destiny.core.astrology

import destiny.core.astrology.Apsis.APHELION
import destiny.core.astrology.Apsis.PERIHELION


enum class MeanOscu {
  MEAN, OSCU
}


sealed class LunarApsis(nameKey: String, abbrKey: String,
                        /** 只會用到 PERIHELION , APHELION  */
                        val apsis: Apsis, val meanOscu: MeanOscu) : LunarPoint(nameKey, abbrKey, Star::class.java.name), Comparable<LunarApsis> {

  /**
   * 平均遠地點 , 月孛 , 水之餘 , Black Moon , Lilith，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.APHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.APOGEE_MEAN, gmt);
  </pre> *
   */
  object APOGEE_MEAN : LunarApsis("LunarApsis.APOGEE", "LunarApsis.APOGEE_ABBR", APHELION, MeanOscu.MEAN)

  /**
   * 真實遠地點 , 月孛 , 水之餘 , Black Moon , Lilith，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.APHELION , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarApsis.APOGEE_OSCU, gmt);
  </pre> *
   */
  object APOGEE_OSCU : LunarApsis("LunarApsis.APOGEE", "LunarApsis.APOGEE_ABBR", APHELION, MeanOscu.OSCU)

  /**
   * 平均近地點，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.PERIHELION , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarApsis.PERIGEE_MEAN , gmt);
  </pre> *
   */
  object PERIGEE_MEAN : LunarApsis("LunarApsis.PERIGEE", "LunarApsis.PERIGEE_ABBR", PERIHELION, MeanOscu.MEAN)

  /**
   * 真實近地點，計算方法，以下兩者結果相同 :
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.PERIHELION , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarApsis.PERIGEE_OSCU , gmt);
  </pre> *
   */
  object PERIGEE_OSCU : LunarApsis("LunarApsis.PERIGEE", "LunarApsis.PERIGEE_ABBR", PERIHELION, MeanOscu.OSCU)


  override fun compareTo(other: LunarApsis): Int {
    if (this == other)
      return 0
    return array.indexOf(this) - array.indexOf(other)
  }

  companion object {
    val array by lazy { arrayOf(APOGEE_MEAN, APOGEE_OSCU, PERIGEE_MEAN, PERIGEE_OSCU) }
  }
}
