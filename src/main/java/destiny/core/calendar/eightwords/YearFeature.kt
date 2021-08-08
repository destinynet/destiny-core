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
import kotlinx.serialization.Serializable

/**
 * 315.0 : 立春
 * 270.0 : 冬至
 */
@Serializable
class YearConfig {
  var changeYearDegree: Double = 315.0
    set(value) {
      require(value > 180 && value < 360) { "changeYearDegree should between 180 and 360" }
      field = value
    }

  init {
    this.changeYearDegree = changeYearDegree
  }

}

class YearFeature(
  private val starPositionImpl: IStarPosition<*>,
  private val julDayResolver: JulDayResolver
) : Feature<YearConfig, StemBranch> {

  override val key: String = "year"

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: YearConfig.() -> Unit): StemBranch {
    val cfg = YearConfig().apply(block)
    return getYear(gmtJulDay, loc, cfg.changeYearDegree, julDayResolver, starPositionImpl)
  }
}
