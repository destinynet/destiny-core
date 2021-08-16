/**
 * Created by smallufo on 2021-08-08.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable


@Serializable
data class YearMonthConfig(

  val yearConfig: YearConfig = YearConfigBuilder.yearConfig(),

  /** 南半球月令是否對沖  */
  val southernHemisphereOpposition: Boolean = false,
  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR,

  val moonImpl: MoonImpl = MoonImpl.SolarTerms
) {
  enum class MoonImpl {
    /** 標準, 節氣劃分月令 */
    SolarTerms,

    /** 120柱月令 */
    SunSign
  }
}

@DestinyMarker
class MonthConfigBuilder : Builder<YearMonthConfig> {

  private var yearConfig: YearConfig = YearConfig()

  fun year(block: YearConfigBuilder.() -> Unit) {
    this.yearConfig = YearConfigBuilder.yearConfig(block)
  }

  /** 南半球月令是否對沖  */
  var southernHemisphereOpposition: Boolean = false

  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR

  var monthImpl: YearMonthConfig.MoonImpl = YearMonthConfig.MoonImpl.SolarTerms

  override fun build(): YearMonthConfig {
    return YearMonthConfig(yearConfig, southernHemisphereOpposition, hemisphereBy, monthImpl)
  }

  companion object {
    fun monthConfig(block: MonthConfigBuilder.() -> Unit = {}): YearMonthConfig {
      return MonthConfigBuilder().apply(block).build()
    }
  }
}


class MonthFeature(
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val julDayResolver: JulDayResolver
) : Feature<YearMonthConfig, IStemBranch> {
  override val key: String = "month"

  override val defaultConfig: YearMonthConfig = YearMonthConfig()

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl, julDayResolver)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: YearMonthConfig): IStemBranch {
    // 原始 月干支
    val originalMonth = getMonth(
      gmtJulDay,
      loc,
      solarTermsImpl,
      starPositionImpl,
      config.southernHemisphereOpposition,
      config.hemisphereBy,
      config.yearConfig.changeYearDegree,
      julDayResolver
    )

    return when (config.moonImpl) {
      YearMonthConfig.MoonImpl.SolarTerms -> originalMonth
      YearMonthConfig.MoonImpl.SunSign    -> {
        // 目前的節氣
        val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

        if (solarTerms.major) {
          // 單數 : 立春 、 驚蟄 ...
          StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!.prev
        } else {
          StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!
        }
      }
    }
  }

}
