/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.eightwords.YearFeature
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YearType
import destiny.tools.AbstractCachedFeature
import jakarta.inject.Named
import java.time.temporal.ChronoField

@Named
class LunarStationYearlyFeature(private val yearFeature: YearFeature,
                                private val chineseDateFeature: ChineseDateFeature,
                                private val julDayResolver: JulDayResolver) : AbstractCachedFeature<ILunarStationConfig, YearIndex>() {

  override val key: String = "lsYearly"

  override val defaultConfig: ILunarStationConfig = LunarStationConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: ILunarStationConfig): YearIndex {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val epoch = when (config.yearEpoch) {
      YearEpoch.EPOCH_1564 -> 1564
      YearEpoch.EPOCH_1864 -> 1864
    }

    val diffValue = lmt.get(ChronoField.YEAR) - epoch


    val (yearSb, yearSb2) = if (config.yearType == YearType.YEAR_SOLAR) {
      // 節氣立春換年
      val yearSb: StemBranch = yearFeature.getModel(gmtJulDay, loc)
      // 以七月再算一次 年干支
      val yearSb2: StemBranch = yearFeature.getModel(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc)
      yearSb to yearSb2
    } else {
      val dayHourConfig = config.dayHourConfig

      // 陰曆初一換年
      val yearSb = chineseDateFeature.getModel(lmt, loc, dayHourConfig).year
      // 以七月再算一次 年干支
      val yearSb2: StemBranch = chineseDateFeature.getModel(lmt.with(ChronoField.MONTH_OF_YEAR, 7), loc, dayHourConfig).year
      yearSb to yearSb2
    }

    val value = (if (yearSb == yearSb2)
      diffValue
    else
      diffValue - 1).let {
      it % 420
    }.let {
      if (it < 0)
        it + 420
      else
        it
    }

    return YearIndex(value, epoch)

  }
}
