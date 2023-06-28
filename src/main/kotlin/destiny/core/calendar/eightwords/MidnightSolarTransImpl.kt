/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 02:05:04
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.Planet
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransPoint
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 以太陽過天底的時間來決定『子正』
 */
class MidnightSolarTransImpl(private val riseTransImpl: IRiseTrans,
                             private val atmosphericPressure: Double = 1013.25,
                             private val atmosphericTemperature: Double = 0.0,
                             val discCenter: Boolean = true,
                             val refraction: Boolean = true) : IMidnight,
                                                               Descriptive by MidnightImpl.NADIR.asDescriptive(),
                                                               Serializable {

  /** 以太陽過當地天底的時間來決定 「子正」 , 回傳 GMT 時刻  */
  override fun getNextMidnight(gmtJulDay: GmtJulDay, loc: ILocation): GmtJulDay {
    val transConfig = TransConfig(discCenter, refraction, atmosphericTemperature, atmosphericPressure)
    return riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, loc, transConfig)!!
  }
}
