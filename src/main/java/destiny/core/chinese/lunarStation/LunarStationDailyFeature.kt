/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.虛
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.IDayHourFeature
import destiny.core.calendar.eightwords.IHourBranchFeature
import destiny.core.calendar.eightwords.MidnightFeature
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.Feature
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField


/**
 * 日禽 實作
 */
class LunarStationDailyFeature(private val hourBranchFeature: IHourBranchFeature,
                               private val dayHourFeature: IDayHourFeature,
                               private val midnightFeature: MidnightFeature,
                               private val julDayResolver: JulDayResolver) : Feature<DayHourConfig, DayIndex> {

  override val key: String = "lsDaily"

  override val defaultConfig: DayHourConfig = DayHourConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayHourConfig): DayIndex {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayHourConfig): DayIndex {

    val (_: StemBranch, hour: StemBranch) = dayHourFeature.getModel(lmt, loc, config)

    val noon = lmt.with(ChronoField.HOUR_OF_DAY, 12)
      .with(ChronoField.MINUTE_OF_HOUR, 0)
      .with(ChronoField.SECOND_OF_MINUTE, 0)

    val noonJulDay = TimeTools.getGmtJulDay(noon).value.toInt().let {


      if (hour.branch == Branch.子) {
        if (lmt.get(ChronoField.HOUR_OF_DAY) > 12) {
          // 24 時之前
          if (config.dayConfig.changeDayAfterZi) {
            // 子初換日
            it + 1
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc, config).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it
              else
                it + 1
            }
          }
        } else {
          // 0時之後
          if (config.dayConfig.changeDayAfterZi) {
            // 子初換日
            it
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc, config).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it - 1
              else
                it
            }
          }
        }
      } else {
        // 其他時辰
        it
      }
    }

    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */
    // 0 .. 419
    val index420 = ((noonJulDay - epoch) % 420).let {
      if (it < 0)
        it + 420
      else
        it
    }
    return DayIndex(index420)
  }

  /**
   * 下個子初 與 下個子正 的差距 , 取絕對值
   */
  private fun getNextZiMidnightDuration(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayHourConfig): Duration {

    val nextMidnight = run {
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
      val gmtResultJulDay = midnightFeature.getModel(gmtJulDay, loc, config.dayConfig)
      val gmtResult = julDayResolver.getLocalDateTime(gmtResultJulDay)
      TimeTools.getLmtFromGmt(gmtResult, loc)
    }

    val nextZiStart = hourBranchFeature.getLmtNextStartOf(lmt, loc, Branch.子, config.hourBranchConfig)
    return Duration.between(nextZiStart, nextMidnight).abs()
  }

  companion object {
    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */
    private const val epoch: Int = 2449271

    fun getLeader(yuan: Int, general: Int): LunarStation {
      require(yuan in 1..7)
      require(general in 1..4)
      return 虛.next((yuan - 1) * 60 + (general - 1) * 15)
    }
  }
}
