/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 02:05:04
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.Planet
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransPoint
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable
import java.util.*

/**
 * 以太陽過天底的時間來決定『子正』
 */
@Impl([Domain(Domains.KEY_MIDNIGHT, MidnightSolarTransImpl.VALUE, default = true)])
class MidnightSolarTransImpl(private val riseTransImpl: IRiseTrans,
                             val atmosphericPressure: Double = 1013.25,
                             val atmosphericTemperature: Double = 0.0,
                             val discCenter: Boolean = true,
                             val refraction: Boolean = true) : IMidnight, Serializable {

  /** 以太陽過當地天底的時間來決定 「子正」 , 回傳 GMT 時刻  */
  override fun getNextMidnight(gmtJulDay: GmtJulDay, loc: ILocation): GmtJulDay {
    val transConfig = TransConfig(discCenter, refraction, atmosphericTemperature, atmosphericPressure)
    return riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, loc, transConfig)!!
  }


  override fun toString(locale: Locale): String {
    return DayConfig.MidnightImpl.NADIR.toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return DayConfig.MidnightImpl.NADIR.getDescription(locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MidnightSolarTransImpl

    if (atmosphericPressure != other.atmosphericPressure) return false
    if (atmosphericTemperature != other.atmosphericTemperature) return false
    if (discCenter != other.discCenter) return false
    if (refraction != other.refraction) return false

    return true
  }

  override fun hashCode(): Int {
    var result = atmosphericPressure.hashCode()
    result = 31 * result + atmosphericTemperature.hashCode()
    result = 31 * result + discCenter.hashCode()
    result = 31 * result + refraction.hashCode()
    return result
  }

  companion object {
    const val VALUE: String = "st"
  }


}
