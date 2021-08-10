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
data class MonthConfig(

  val yearConfig: YearConfig = YearConfigBuilder.yearConfig(),

  /** 南半球月令是否對沖  */
  val southernHemisphereOpposition: Boolean = false,
  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR,

  val impl: Impl = Impl.SolarTerms
) {
  enum class Impl {
    /** 標準, 節氣劃分月令 */
    SolarTerms,

    /** 120柱月令 */
    SunSign
  }
}

interface IMonthConfigBuilder : IYearConfigBuilder {

  fun yearConfig(block : IYearConfigBuilder.() -> Unit = {})

  var southernHemisphereOpposition: Boolean

  var hemisphereBy: HemisphereBy

  var monthImpl: MonthConfig.Impl

}

@DestinyMarker
class MonthConfigBuilder(private val yearConfigBuilder: YearConfigBuilder = YearConfigBuilder()) : Builder<MonthConfig>, IMonthConfigBuilder,
                                                                                                   IYearConfigBuilder by yearConfigBuilder {

  var yearConfig: YearConfig = YearConfig()

  override fun yearConfig(block: IYearConfigBuilder.() -> Unit) {

    this.yearConfig = yearConfigBuilder.apply(block).build()
  }

  /** 南半球月令是否對沖  */
  override var southernHemisphereOpposition: Boolean = false

  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  override var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR

  override var monthImpl: MonthConfig.Impl = MonthConfig.Impl.SolarTerms

  override fun build(): MonthConfig {
    return MonthConfig(yearConfig, southernHemisphereOpposition, hemisphereBy, monthImpl)
  }

  companion object {
    fun monthConfig(block: MonthConfigBuilder.() -> Unit = {}): MonthConfig {
      return MonthConfigBuilder().apply(block).build()
    }
  }
}


class MonthFeature(
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val julDayResolver: JulDayResolver
) : Feature<MonthConfig, IStemBranch> {
  override val key: String = "month"

  override val defaultConfig: MonthConfig = MonthConfig()

  override val builder: Builder<MonthConfig> = MonthConfigBuilder()


  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl, julDayResolver)
  }

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: MonthConfig): IStemBranch {
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

    return when (config.impl) {
      MonthConfig.Impl.SolarTerms -> originalMonth
      MonthConfig.Impl.SunSign    -> {
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
