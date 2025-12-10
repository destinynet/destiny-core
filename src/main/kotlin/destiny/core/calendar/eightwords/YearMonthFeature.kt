/**
 * Created by smallufo on 2021-08-08.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import jakarta.inject.Named
import kotlinx.serialization.Serializable

enum class MonthImpl {
  /** 標準, 節氣劃分月令 */
  SolarTerms,

  /** 120柱月令 */
  SunSign
}

@Serializable
data class MonthConfig(
  /** 南半球月令是否對沖  */
  override var southernHemisphereOpposition: Boolean = false,
  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  override var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR,

  override var monthImpl: MonthImpl = MonthImpl.SolarTerms
) : IMonthConfig


class MonthConfigBuilder : Builder<MonthConfig> {

  /** 南半球月令是否對沖  */
  var southernHemisphereOpposition: Boolean = false

  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR

  var monthImpl: MonthImpl = MonthImpl.SolarTerms

  override fun build(): MonthConfig {
    return MonthConfig(southernHemisphereOpposition, hemisphereBy, monthImpl)
  }

  companion object {
    fun monthConfig(block: MonthConfigBuilder.() -> Unit = {}): MonthConfig {
      return MonthConfigBuilder().apply(block).build()
    }
  }
}

@Serializable
data class YearMonthConfig(
  override val yearConfig: YearConfig = YearConfig(),
  override val monthConfig: MonthConfig = MonthConfig()
) : IYearMonthConfig , IYearConfig by yearConfig , IMonthConfig by monthConfig

@DestinyMarker
class YearMonthConfigBuilder(val iYearConfig : IYearConfig, val iMonthConfig: IMonthConfig) : Builder<YearMonthConfig> {

  override fun build(): YearMonthConfig {
    return YearMonthConfig(iYearConfig.yearConfig, iMonthConfig.monthConfig)
  }

  companion object {
    context(yearConfig: IYearConfig, monthConfig : IMonthConfig)
    fun yearMonthConfig(block: YearMonthConfigBuilder.() -> Unit = {}): YearMonthConfig {
      return YearMonthConfigBuilder(yearConfig, monthConfig).apply(block).build()
    }
  }
}

data class MonthRange(val year: IStemBranch, val month: IStemBranch, override val begin: GmtJulDay, override val end: GmtJulDay) : IEventSpan

interface IYearMonthFeature : Feature<IYearMonthConfig, IStemBranch> {

  fun getMonthRange(gmtJulDay: GmtJulDay, loc: ILocation, config: IYearMonthConfig): MonthRange

  fun getMonthRanges(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, config: IYearMonthConfig): List<MonthRange> {
    require(fromGmt < toGmt) { "Invalid. from = $fromGmt , to = $toGmt" }

    return generateSequence(getMonthRange(fromGmt, loc, config)) { monthRange ->
      getMonthRange(monthRange.end + 0.01, loc, config)
    }.takeWhile {
      it.begin in fromGmt..toGmt || it.end in fromGmt..toGmt
        || it.begin < fromGmt && toGmt < it.end
    }.toList()
  }
}

/**
 * 月干支
 */
@Named
class YearMonthFeature(private val starPositionImpl: IStarPosition<*>,
                       private val starTransitImpl: IStarTransit,
                       private val julDayResolver: JulDayResolver) : IYearMonthFeature, AbstractCachedFeature<IYearMonthConfig, IStemBranch>() {

  override val key: String = "month"

  override val defaultConfig: IYearMonthConfig = YearMonthConfig()

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl)
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IYearMonthConfig): IStemBranch {
    // 原始 月干支
    val originalMonth = getMonth(
      gmtJulDay,
      loc,
      solarTermsImpl,
      starPositionImpl,
      config.monthConfig.southernHemisphereOpposition,
      config.monthConfig.hemisphereBy,
      config.yearConfig.changeYearDegree,
      julDayResolver
    )

    return when (config.monthConfig.monthImpl) {
      MonthImpl.SolarTerms -> originalMonth
      MonthImpl.SunSign    -> {
        // 目前的節氣
        val solarTerms = solarTermsImpl.getSolarTerms(gmtJulDay)

        if (solarTerms.major) {
          // 單數 : 立春 、 驚蟄 ...
          StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!.prev
        } else {
          StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!
        }
      }
    }
  }

  override fun getMonthRange(gmtJulDay: GmtJulDay, loc: ILocation, config: IYearMonthConfig): MonthRange {
    val year = getYear(gmtJulDay, loc, config.changeYearDegree, julDayResolver, starPositionImpl)
    val month = getMonth(gmtJulDay, loc, solarTermsImpl, starPositionImpl, config.southernHemisphereOpposition, config.hemisphereBy, config.changeYearDegree, julDayResolver)
    val (begin, end) = solarTermsImpl.getMajorSolarTermsGmtBetween(gmtJulDay).let { (p1, p2) -> p1.begin to p2.begin }
    return MonthRange(year, month, begin, end)
  }


}
