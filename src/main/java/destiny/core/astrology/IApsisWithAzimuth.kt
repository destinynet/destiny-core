/**
 * @author smallufo
 * Created on 2007/5/28 at 上午 4:22:14
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

/**
 * 計算星球南北交點
 * Swiss Ephemeris 實作是 ApsisWithAzimuthImpl
 */
interface IApsisWithAzimuth : IApsis {

  fun getPositionsWithAzimuths(gmtJulDay: GmtJulDay, loc: ILocation, config: ApsisAzimuthConfig): Map<Apsis, StarPosWithAzimuth>

  fun getPositionWithAzimuth(gmtJulDay: GmtJulDay, loc: ILocation, apsis: Apsis, config: ApsisAzimuthConfig): StarPosWithAzimuth {
    val map = getPositionsWithAzimuths(gmtJulDay, loc, config)
    return map[apsis] ?: throw RuntimeException("Cannot found StarPositionWithAzimuth of the Apsis : $apsis")
  }

}
