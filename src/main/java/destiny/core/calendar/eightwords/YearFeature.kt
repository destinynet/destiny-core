/**
 * Created by smallufo on 2021-08-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.StemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable

/**
 * 315.0 : 立春
 * 270.0 : 冬至
 */
@Serializable
data class YearConfig(val changeYearDegree: Double = 315.0): java.io.Serializable {

  init {
    require(changeYearDegree > 180 && changeYearDegree < 360)
    { "degree should between 180 and 360" }
  }
}

@DestinyMarker
class YearConfigBuilder : Builder<YearConfig> {

  var changeYearDegree = 315.0

  override fun build(): YearConfig {
    return YearConfig(changeYearDegree)
  }

  companion object {
    fun yearConfig(block: YearConfigBuilder.() -> Unit = {}): YearConfig {
      return YearConfigBuilder().apply(block).build()
    }
  }
}

class YearFeature(private val starPositionImpl: IStarPosition<*>,
                  private val julDayResolver: JulDayResolver) : AbstractCachedFeature<YearConfig, StemBranch>() {

  override val key: String = "year"

  override val defaultConfig: YearConfig = YearConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: YearConfig): StemBranch {
    return getYear(gmtJulDay, loc, config.changeYearDegree, julDayResolver, starPositionImpl)
  }
}
