/**
 * Created by smallufo on 2021-08-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.StemBranch
import destiny.tools.Feature

/**
 * 315.0 : 立春
 * 270.0 : 冬至
 */
class YearConfig(var changeYearDegree: Double = 315.0)

interface IYearProcessor {
  fun getYearModel(gmtJulDay: GmtJulDay, location: ILocation, yearConfig: YearConfig): StemBranch
}

class YearFeature(private val defaultConfig: YearConfig) : Feature<YearConfig, IYearProcessor, StemBranch> {

  override val key: String = "year"

  override fun IYearProcessor.getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: YearConfig.() -> Unit): StemBranch {
    val cfg = defaultConfig.apply(block)
    return this.getYearModel(gmtJulDay, loc, cfg)
  }
}

class YearProcessorImpl(private val starPositionImpl: IStarPosition<*> ,
                        private val julDayResolver: JulDayResolver) : IYearProcessor {

  override fun getYearModel(gmtJulDay: GmtJulDay, location: ILocation, yearConfig: YearConfig): StemBranch {
    val yearEclipticDegreeImpl = YearEclipticDegreeImpl(yearConfig.changeYearDegree , starPositionImpl, julDayResolver)
    return yearEclipticDegreeImpl.getYear(gmtJulDay, location)
  }
}
