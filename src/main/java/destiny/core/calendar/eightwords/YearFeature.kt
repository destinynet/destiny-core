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

class YearFeature(private val defaultConfig: YearConfig,
                  private val starPositionImpl: IStarPosition<*> ,
                  private val julDayResolver: JulDayResolver) : Feature<YearConfig, StemBranch> {

  override val key: String = "year"

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: YearConfig.() -> Unit): StemBranch {
    val cfg = defaultConfig.apply(block)
    val yearEclipticDegreeImpl = YearEclipticDegreeImpl(cfg.changeYearDegree , starPositionImpl, julDayResolver)
    return yearEclipticDegreeImpl.getYear(gmtJulDay, loc)
  }
}

