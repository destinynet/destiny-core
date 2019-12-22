/**
 * Created by smallufo on 2019-03-02.
 */
package destiny.fengshui.sanyuan

import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

interface IYuan {

  fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan


  companion object {
    /** 年紫白入中 */
    fun getCenter(yuan: Yuan , year: StemBranch) : Int {
      val steps = year.getAheadOf(StemBranch.甲子)
      return when(yuan) {
        Yuan.UP -> FlyingStar.getValue(1 , steps , true)
        Yuan.MID-> FlyingStar.getValue(4 , steps , true)
        Yuan.LOW-> FlyingStar.getValue(7 , steps , true)
      }
    }

  }

}

class YuanImpl(val solarTermsImpl: ISolarTerms) : IYuan, Serializable {

  override fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan {

    val gmt = TimeTools.getGmtFromLmt(lmt, loc)
    val prolepticYear = gmt.get(ChronoField.YEAR)

    return if ((prolepticYear - 1864) % 60 != 0) {
      Yuan.getYuan(prolepticYear)
    } else {
      // 每 60年交會，要特別計算 立春
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
      val startOfYear = gmt.with(LocalDate.of(prolepticYear, 1, 1))
        .with(LocalTime.of(0, 0))
      val startOfYearGmtJulDay = TimeTools.getGmtJulDay(startOfYear)
      // 立春JD
      val julDayOfSpring = solarTermsImpl.getSolarTermsTime(SolarTerms.立春, startOfYearGmtJulDay, true)
      if (gmtJulDay >= julDayOfSpring) {
        Yuan.getYuan(prolepticYear)
      } else {
        Yuan.getYuan(prolepticYear - 1)
      }
    }
  }

}
