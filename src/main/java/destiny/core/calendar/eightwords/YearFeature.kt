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

//  init {
//    require(changeYearDegree > 180 && changeYearDegree < 360)
//    { "degree should between 180 and 360" }
//  }

  var changeYearDegree: Double = 315.0
    set(value) {
      require(value > 180 && value < 360) { "changeYearDegree should between 180 and 360" }
      field = value
    }

  init {
    this.changeYearDegree = changeYearDegree
  }
}

class YearConfigBuilder {
  var changeYearDegree = 315.0
}

fun YearConfig(block: YearConfigBuilder.() -> Unit = {}): YearConfig {
  val builder = YearConfigBuilder().apply(block)
  return YearConfig().apply {
    changeYearDegree = builder.changeYearDegree
  }
}

class YearFeature(
  private val starPositionImpl: IStarPosition<*>,
  private val julDayResolver: JulDayResolver
) : Feature<YearConfig, StemBranch> {

  override val key: String = "year"


  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: YearConfig): StemBranch {
    return getYear(gmtJulDay, loc, config.changeYearDegree, julDayResolver, starPositionImpl)
  }


  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: YearConfig.() -> Unit): StemBranch {
    val config = YearConfig().apply(block)
    return getModel(gmtJulDay, loc, config)
    //return getYear(gmtJulDay, loc, config.changeYearDegree, julDayResolver, starPositionImpl)
  }
}
