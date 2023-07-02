/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.IDayHourConfig
import destiny.core.calendar.eightwords.YearFeature
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YearType
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.temporal.ChronoField


@Serializable
data class YearlyConfig(override var yearType: YearType = YearType.YEAR_SOLAR,
                        override var yearEpoch: YearEpoch = YearEpoch.EPOCH_1564,
                        override val dayHourConfig: DayHourConfig = DayHourConfig()): IYearlyConfig ,
                                                                                      IDayHourConfig by dayHourConfig

context(IDayHourConfig)
@DestinyMarker
class YearlyConfigBuilder : Builder<YearlyConfig> {
  var yearType: YearType = YearType.YEAR_SOLAR
  var yearEpoch: YearEpoch = YearEpoch.EPOCH_1564

//  var dayHourConfig: DayHourConfig = DayHourConfig()
//  fun dayConfig(block: DayHourConfigBuilder.() -> Unit = {}) {
//    dayHourConfig = DayHourConfigBuilder.dayHour(block)
//  }

  override fun build(): YearlyConfig {
    return YearlyConfig(yearType, yearEpoch)
  }

  companion object {
    context(IDayHourConfig)
    fun yearly(block: YearlyConfigBuilder.() -> Unit = {}) : YearlyConfig {
      return YearlyConfigBuilder().apply(block).build()
    }
  }
}

@Named
class LunarStationYearlyFeature(private val yearFeature: YearFeature,
                                private val chineseDateFeature: ChineseDateFeature,
                                private val julDayResolver: JulDayResolver) : AbstractCachedFeature<IYearlyConfig, YearIndex>() {

  override val key: String = "lsYearly"

  override val defaultConfig: YearlyConfig = YearlyConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IYearlyConfig): YearIndex {

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
      val dayHourConfig = (config as YearlyConfig).dayHourConfig

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
