/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.DayNight
import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import java.io.Serializable


class DayNightHalfImpl(private val riseTransImpl: IRiseTrans) : IDayNight, Serializable {

  // TODO : 極區內可能不適用
  override fun getDayNight(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): DayNight {
    val nextMeridianJulDay = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, loc, transConfig)!!
    val nextNadirJulDay = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, loc, transConfig)!!

    return if (nextNadirJulDay > nextMeridianJulDay) {
      //子正到午正（上半天）
      DayNight.DAY
    } else {
      //午正到子正（下半天）
      DayNight.NIGHT
    }
  }
}
