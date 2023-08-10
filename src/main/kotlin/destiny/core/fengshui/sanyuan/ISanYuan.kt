/**
 * Created by smallufo on 2019-03-02.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.chinese.Yuan
import destiny.core.chinese.StemBranch
import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import kotlin.math.abs

/** 三元 */
interface ISanYuan {

  fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan

  companion object {
    /**
     * [Yuan.UP]  // 上元 , since 1864 立春
     * [Yuan.MID] // 中元 , since 1924 立春
     * [Yuan.LOW] // 下元 , since 1984 立春
     *
     * 從西元的年份推算，當時是哪元 . NOTE : 必須是立春後
     * @param year : 為 proleptic year .
     * 1=西元元年
     * 0=西元前1年
     * -1=西元前2年
     **/
    fun getYuan(year: Int): Yuan {
      val gap180 = (year - 1864).let {
        if (it >= 0)
          it
        else
          180 - abs(it) % 180
      } % 180

      return when (gap180 / 60 + 1) {
        1 -> Yuan.UP
        2 -> Yuan.MID
        else -> Yuan.LOW
      }
    }

    /** 年紫白入中 */
    fun getCenter(yuan: Yuan, year: StemBranch): Period {
      val steps = year.getAheadOf(StemBranch.甲子)
      return when (yuan) {
        Yuan.UP  -> FlyingStar.getValue(1.toPeriod(), steps, true)
        Yuan.MID -> FlyingStar.getValue(4.toPeriod(), steps, true)
        Yuan.LOW -> FlyingStar.getValue(7.toPeriod(), steps, true)
      }
    }
  }
}

class SanYuanImpl(val solarTermsImpl: ISolarTerms) : ISanYuan, Serializable {

  override fun getYuan(lmt: ChronoLocalDateTime<*>, loc: ILocation): Yuan {

    val gmt = TimeTools.getGmtFromLmt(lmt, loc)
    val prolepticYear = gmt.get(ChronoField.YEAR)

    return if ((prolepticYear - 1864) % 60 != 0) {
      ISanYuan.getYuan(prolepticYear)
    } else {
      // 每 60年交會，要特別計算 立春
      val gmtJulDay = lmt.toGmtJulDay(loc)
      val startOfYear = gmt.with(LocalDate.of(prolepticYear, 1, 1))
        .with(LocalTime.of(0, 0))
      val startOfYearGmtJulDay = TimeTools.getGmtJulDay(startOfYear)
      // 立春JD
      val julDayOfSpring = solarTermsImpl.getSolarTermsTime(SolarTerms.立春, startOfYearGmtJulDay, true)
      if (gmtJulDay >= julDayOfSpring) {
        ISanYuan.getYuan(prolepticYear)
      } else {
        ISanYuan.getYuan(prolepticYear - 1)
      }
    }
  }

}
