/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.StemBranch
import mu.KotlinLogging
import java.io.Serializable


/**
 * 依據太陽在黃道帶 (Ecliptic) 的度數 (Degree) 來切割年份
 */
class YearEclipticDegreeImpl(
  /** 換年的度數 , 通常是立春點 (315) 換年 */
  override val changeYearDegree: Double = 315.0,
  private val starPositionImpl: IStarPosition<*>,
  private val julDayResolver: JulDayResolver) : IYear , Serializable {

  init {
    require(changeYearDegree > 180 && changeYearDegree < 360) { "changeYearDegree should between 180 and 360" }
  }

  override fun getYear(gmtJulDay: GmtJulDay, loc: ILocation): StemBranch {
    val feature = YearFeature(starPositionImpl, julDayResolver)
    return feature.getModel(gmtJulDay, loc, YearConfig(changeYearDegree))
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is YearEclipticDegreeImpl) return false

    if (changeYearDegree != other.changeYearDegree) return false

    return true
  }

  override fun hashCode(): Int {
    return changeYearDegree.hashCode()
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }
}
