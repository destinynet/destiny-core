/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IRiseTrans
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.Builder
import destiny.tools.Feature
import java.time.chrono.ChronoLocalDateTime


class DayHourFeature(
  private val midnightFeature: MidnightFeature,
  private val midnightImpl: IMidnight,
  private val riseTransImpl: IRiseTrans,
  private val julDayResolver: JulDayResolver
) : Feature<HourConfig, Pair<StemBranch, StemBranch>> {

  override val key: String = "dayHour"

  override val defaultConfig: HourConfig = HourConfig()

  override val builder: Builder<HourConfig> = HourConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourConfig): Pair<StemBranch, StemBranch> {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    val hourImpl = getHourImpl(config.impl, riseTransImpl, julDayResolver)

    val day: StemBranch = getDay(lmt, loc, hourImpl, midnightImpl, config.dayConfig.changeDayAfterZi, julDayResolver)

    val hourBranch = getHourBranch(config.impl , lmt, loc)
    val hourStem = getHourStem(hourImpl, lmt, loc, day, hourBranch, config.dayConfig.changeDayAfterZi, midnightImpl, julDayResolver)

    val hour = StemBranch[hourStem, hourBranch]
    return day to hour
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: HourConfig): Pair<StemBranch, StemBranch> {

    val hourImpl = getHourImpl(config.impl, riseTransImpl, julDayResolver)
    val day: StemBranch = getDay(lmt, loc, hourImpl, midnightImpl, config.dayConfig.changeDayAfterZi, julDayResolver)

    val hourBranch = getHourBranch(config.impl , lmt, loc)
    val hourStem = getHourStem(hourImpl, lmt, loc, day, hourBranch, config.dayConfig.changeDayAfterZi, midnightImpl, julDayResolver)

    val hour = StemBranch[hourStem, hourBranch]
    return day to hour
  }

  private fun getHourBranch(impl : HourConfig.Impl , lmt: ChronoLocalDateTime<*> , loc: ILocation): Branch {
    return when (impl) {
      HourConfig.Impl.TST -> {
        val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
        getHourBranchByTst(gmtJulDay, loc, riseTransImpl)
      }
      HourConfig.Impl.LMT -> {
        getHourBranchByLmt(lmt)
      }
    }
  }

  private fun getHourStem(
    hourImpl: IHour,
    lmt: ChronoLocalDateTime<*>,
    loc: ILocation,
    day: StemBranch,
    hourBranch: Branch,
    cdaz: Boolean,
    midnightImpl: IMidnight,
    julDayResolver: JulDayResolver
  ): Stem {
    val nextZi: ChronoLocalDateTime<*> = hourImpl.getLmtNextStartOf(lmt, loc, Branch.子, julDayResolver)

    val tempDayStem = day.stem.let {
      // 如果「子正」才換日
      if (!cdaz) {
        /**
         * <pre>
         * 而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
         * 第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
         * 第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
         *
         * 子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
         * |------------------|--------------------|--------------------|------------------|
        </pre> *
         */

        if (day !== getDay(nextZi, loc, hourImpl, midnightImpl, cdaz, julDayResolver))
          it.next
        else
          it
      } else
        it
    }
    // 時干
    return StemBranchUtils.getHourStem(tempDayStem, hourBranch)
  }
}
