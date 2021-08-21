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
import destiny.tools.Feature


class ChineseDateFeature(private val chineseDateImpl : IChineseDate,
                         private val dayHourFeature: IDayHourFeature,
                         private val midnightFeature: MidnightFeature,
                         private val julDayResolver: JulDayResolver) : Feature<DayHourConfig , ChineseDate> {
  override val key: String = "chineseDate"

  override val defaultConfig: DayHourConfig = DayHourConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayHourConfig): ChineseDate {

    val (day: StemBranch, hour: StemBranch) = dayHourFeature.getModel(gmtJulDay, loc, config)

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    return chineseDateImpl.getChineseDate(lmt, loc, day, hour, midnightFeature, config.dayConfig.changeDayAfterZi, julDayResolver)
  }
}
