/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.astrology.IDayNight
import destiny.astrology.IRiseTrans
import destiny.astrology.Planet
import destiny.astrology.TransPoint
import destiny.core.DayNight
import destiny.core.calendar.ILocation
import java.io.Serializable
import java.util.*

class DayNightHalfImpl(private val riseTransImpl: IRiseTrans) : IDayNight, Serializable {

  override fun getDayNight(gmtJulDay: Double, location: ILocation): DayNight {
    val nextMeridianJulDay = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location,
                                                             discCenter = false, refraction = true)
    val nextNadirJulDay = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location,
                                                          discCenter = false, refraction = true)

    return if (nextNadirJulDay > nextMeridianJulDay) {
      //子正到午正（上半天）
      DayNight.DAY
    } else {
      //午正到子正（下半天）
      DayNight.NIGHT
    }
  }


  override fun getTitle(locale: Locale): String {
    return "前半天後半天"
  }

  override fun getDescription(locale: Locale): String {
    return "夜半子正至午正（前半天）為晝；中午至半夜（後半天）為夜"
  }
}
