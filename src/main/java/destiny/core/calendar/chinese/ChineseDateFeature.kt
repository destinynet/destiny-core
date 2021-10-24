/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.IDayHourFeature
import destiny.core.calendar.eightwords.MidnightFeature
import destiny.core.chinese.StemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import java.time.chrono.ChronoLocalDate
import javax.inject.Named


interface IChineseDateFeature : Feature<DayHourConfig, ChineseDate> {

  /** 列出該月有幾日  */
  fun getDaysOf(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int

  // =============== 陰曆轉陽曆 ===============
  fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate

  fun getYangDate(cdate: ChineseDate): ChronoLocalDate {
    return getYangDate(cdate.cycleOrZero, cdate.year, cdate.leapMonth, cdate.month, cdate.day)
  }
}

@Named
class ChineseDateFeature(private val chineseDateImpl : IChineseDate,
                         private val dayHourFeature: IDayHourFeature,
                         private val midnightFeature: MidnightFeature,
                         private val julDayResolver: JulDayResolver) : AbstractCachedFeature<DayHourConfig, ChineseDate>() , IChineseDateFeature {
  override val key: String = "chineseDate"

  override val defaultConfig: DayHourConfig = DayHourConfig()

  /** 列出該月有幾日  */
  override fun getDaysOf(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int {
    // 以當月初一開始
    val date = ChineseDate(cycle, year, month, leap, 1)
    // 推算下個月初一
    val nextMonthStart = chineseDateImpl.nextMonthStart(date)
    // 再往前推算一日
    val monthEnd = chineseDateImpl.minusDays(nextMonthStart, 1)
    return monthEnd.day
  }

  override fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate {
    return chineseDateImpl.getYangDate(cycle, year, leap, month, day)
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DayHourConfig): ChineseDate {

    val (day: StemBranch, hour: StemBranch) = dayHourFeature.getModel(gmtJulDay, loc, config)

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    return chineseDateImpl.getChineseDate(lmt, loc, day, hour, midnightFeature, config.dayConfig.changeDayAfterZi, julDayResolver)
  }
}
