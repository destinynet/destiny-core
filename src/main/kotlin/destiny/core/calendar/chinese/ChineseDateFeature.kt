/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.chinese

import destiny.core.calendar.ConversionException
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toGmtJulDay
import kotlinx.datetime.toKotlinInstant
import destiny.core.calendar.eightwords.ChangeDay
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.IHourBranchFeature
import destiny.core.calendar.eightwords.MidnightFeature
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import jakarta.inject.Named
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField


interface IChineseDateFeature : Feature<ChineseDateConfig, ChineseDate> {

  /** 列出該月有幾日（民曆） */
  fun getDaysOf(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int

  // =============== 陰曆轉陽曆（民曆） ===============
  fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate

  fun getYangDate(cdate: ChineseDate): ChronoLocalDate {
    return getYangDate(cdate.cycleOrZero, cdate.year, cdate.leapMonth, cdate.month, cdate.day)
  }

  /** 該農曆年在此地、此設定下的月份（12 或 13 個） */
  fun monthsOf(year: Int, loc: ILocation, config: ChineseDateConfig = defaultConfig): List<LunarMonth>

  /**
   * 在此地、此設定的日界下，[day] 這一天從哪一刻開始。
   * [DayBoundary.CIVIL] 時 [day] 是民曆時區的日期（午夜）；[DayBoundary.DAY_HOUR] 時是當地鐘面日期，
   * 回的是前一晚的子初或子正（依 `changeDay`），真太陽時的話隨經度與均時差移動。
   */
  fun dayStart(day: LocalDate, loc: ILocation, config: ChineseDateConfig = defaultConfig): GmtJulDay

  /** 此瞬間所在的農曆月；月名與 [getModel] 一致 */
  fun monthOf(gmtJulDay: GmtJulDay, loc: ILocation, config: ChineseDateConfig = defaultConfig): LunarMonth {
    val date = getModel(gmtJulDay, loc, config)
    val year = date.westYear
    return monthsOf(year, loc, config).firstOrNull { it.number == date.month && it.leap == date.leapMonth }
      ?: throw ConversionException("$year 年找不到 $date 所在的月")
  }

  /** [month] 的下一個月 */
  fun nextMonthOf(month: LunarMonth, loc: ILocation, config: ChineseDateConfig = defaultConfig): LunarMonth {
    val months = monthsOf(month.year, loc, config)
    val i = months.indexOfFirst { it.number == month.number && it.leap == month.leap }
    return months.getOrNull(i + 1) ?: monthsOf(month.year + 1, loc, config).first()
  }

  /**
   * [month] 在此設定下從哪一刻開始：[MonthBoundary.INSTANT] 是朔的瞬間；[MonthBoundary.DAY] 是初一那天在本設定日界下的起點。
   * [MonthTable.CIVIL] 的初一是萬年曆的日期。
   */
  fun monthStart(month: LunarMonth, loc: ILocation, config: ChineseDateConfig = defaultConfig): GmtJulDay =
    when (config.lunisolar.monthBoundary) {
      MonthBoundary.INSTANT -> month.newMoon
      MonthBoundary.DAY -> dayStart(month.firstDay, loc, config)
    }
}

/**
 * 「這個瞬間是農曆幾月幾日」。
 *
 * 月表與月名由 [ILunisolarCalendar] 負責；這裡只提供「瞬間屬於哪一天」的日界：
 * [DayBoundary.CIVIL] 是民曆時區的午夜，[DayBoundary.DAY_HOUR] 依八字日柱的設定
 * （時辰切割 × 子初／子正換日），與日柱用同一套換日點，所以農曆的「日」就是日柱的「日」。
 */
@Named
class ChineseDateFeature(private val calendar: ILunisolarCalendar,
                         private val hourBranchFeature: IHourBranchFeature,
                         private val midnightFeature: MidnightFeature,
                         private val julDayResolver: JulDayResolver) : AbstractCachedFeature<ChineseDateConfig, ChineseDate>(), IChineseDateFeature {
  override val key: String = "chineseDate"

  override val defaultConfig: ChineseDateConfig = ChineseDateConfig()

  override fun getDaysOf(cycle: Int, year: StemBranch, month: Int, leap: Boolean): Int {
    return calendar.getDaysOf(cycle, year, month, leap)
  }

  override fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate {
    return calendar.getYangDate(cycle, year, leap, month, day)
  }

  override fun monthsOf(year: Int, loc: ILocation, config: ChineseDateConfig): List<LunarMonth> {
    return calendar.monthsOf(year, config.lunisolar, dayOf(loc, config))
  }

  override fun dayStart(day: LocalDate, loc: ILocation, config: ChineseDateConfig): GmtJulDay {
    return when (config.lunisolar.dayBoundary) {
      DayBoundary.CIVIL -> {
        val noon = day.atTime(12, 0).toGmtJulDay(ZoneOffset.UTC)
        day.atStartOfDay(config.lunisolar.civil.zone(noon)).toInstant().toKotlinInstant().toGmtJulDay()
      }
      DayBoundary.DAY_HOUR -> {
        // 前一天中午之後的第一個換日點，就是這一天的起點
        val lmt = day.minusDays(1).atTime(12, 0)
        val boundary: ChronoLocalDateTime<*> = when (config.dayHourConfig.changeDay) {
          ChangeDay.ZI_BEGIN -> hourBranchFeature.getLmtNextStartOf(lmt, loc, Branch.子, config.dayHourConfig.hourBranchConfig)
          ChangeDay.ZI_MIDDLE -> TimeTools.getLmtFromGmt(midnightFeature.getModel(lmt, loc, config.dayHourConfig.dayConfig), loc, julDayResolver)
        }
        TimeTools.getGmtJulDay(boundary, loc)
      }
    }
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: ChineseDateConfig): ChineseDate {
    return calendar.lunarDate(gmtJulDay, config.lunisolar, dayOf(loc, config))
  }

  private fun dayOf(loc: ILocation, config: ChineseDateConfig): DayOf = when (config.lunisolar.dayBoundary) {
    DayBoundary.CIVIL -> CivilDayOf(config.lunisolar.civil)
    DayBoundary.DAY_HOUR -> DayHourDayOf(loc, config.dayHourConfig)
  }

  /**
   * 依八字日柱的換日法：找出此瞬間之後的下一個換日點（子初或子正）。
   * 換日點在傍晚（通常 23 時前後），這個瞬間屬於換日點那一天；換日點在清晨（0 時前後），屬於前一天。
   * 不是子時的時刻也適用，不必另外判斷時辰。
   */
  private inner class DayHourDayOf(private val loc: ILocation, private val config: DayHourConfig) : DayOf {

    override fun dayOf(gmt: GmtJulDay): LocalDate {
      val lmt = TimeTools.getLmtFromGmt(gmt, loc, julDayResolver)
      val boundary: ChronoLocalDateTime<*> = when (config.changeDay) {
        ChangeDay.ZI_BEGIN -> hourBranchFeature.getLmtNextStartOf(lmt, loc, Branch.子, config.hourBranchConfig)
        ChangeDay.ZI_MIDDLE -> TimeTools.getLmtFromGmt(midnightFeature.getModel(lmt, loc, config.dayConfig), loc, julDayResolver)
      }
      val date = LocalDate.from(boundary.toLocalDate())
      return if (boundary.get(ChronoField.HOUR_OF_DAY) >= 12) date else date.minusDays(1)
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is DayHourDayOf) return false
      return loc == other.loc && config == other.config
    }

    override fun hashCode(): Int = 31 * loc.hashCode() + config.hashCode()

    override fun toString(): String = "DayHourDayOf(loc=$loc, config=$config)"
  }
}
