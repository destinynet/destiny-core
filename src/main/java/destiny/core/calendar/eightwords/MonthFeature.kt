/**
 * Created by smallufo on 2021-08-08.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.tools.Feature


data class MonthConfig(
  /** 南半球月令是否對沖  */
  var southernHemisphereOpposition: Boolean = false,
  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR
)

class MonthFeature(private val yearImpl: IYear,
                   private val starPositionImpl: IStarPosition<*>,
                   private val starTransitImpl: IStarTransit,
                   private val julDayResolver: JulDayResolver) : Feature<MonthConfig, IStemBranch> {
  override val key: String = "month"

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl, julDayResolver)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, block: MonthConfig.() -> Unit): IStemBranch {
    val cfg = MonthConfig().apply(block)
    return getMonth(gmtJulDay, loc, cfg.southernHemisphereOpposition, cfg.hemisphereBy, solarTermsImpl, starPositionImpl, yearImpl)
  }
}
