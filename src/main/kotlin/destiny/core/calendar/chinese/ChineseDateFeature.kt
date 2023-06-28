/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.IHourBranchFeature
import destiny.core.calendar.eightwords.MidnightFeature
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import jakarta.inject.Named
import java.time.chrono.ChronoLocalDate
import java.time.temporal.ChronoUnit


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
                         private val hourBranchFeature: IHourBranchFeature,
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

    val hour = hourBranchFeature.getModel(gmtJulDay, loc, config.hourBranchConfig)

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    return if (hour != Branch.子) {
      // 不是子時，直接傳回今日的日期即可
      chineseDateImpl.getChineseDate(lmt.toLocalDate())
    } else {
      // 子時，要判定是子正之前，還是子正之後

      // 目前這個 LMT 的農曆日期
      val lmtDate = chineseDateImpl.getChineseDate(lmt.toLocalDate())
      // 後一天
      val nextDate = chineseDateImpl.getChineseDate(lmt.toLocalDate().plus(1, ChronoUnit.DAYS))
      // 前一天
      val prevDate = chineseDateImpl.getChineseDate(lmt.toLocalDate().minus(1, ChronoUnit.DAYS))

      val nextMidnightLmt = TimeTools.getLmtFromGmt(midnightFeature.getModel(lmt, loc), loc, julDayResolver)

      // 下個子正的農曆日期
      val nextMidnightDay = chineseDateImpl.getChineseDate(nextMidnightLmt.toLocalDate())

      chineseDateImpl.calculateZi(lmt, lmtDate, nextDate, prevDate, nextMidnightLmt, nextMidnightDay, config.dayConfig.changeDayAfterZi)
    }

    //return chineseDateImpl.getChineseDate(lmt, loc, day, hour, midnightFeature, config.dayConfig.changeDayAfterZi, julDayResolver)
  }
}
