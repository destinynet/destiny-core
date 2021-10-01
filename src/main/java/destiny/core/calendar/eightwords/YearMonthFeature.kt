/**
 * Created by smallufo on 2021-08-08.
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import java.util.*
import javax.inject.Named


@Serializable
data class MonthConfig(
  /** 南半球月令是否對沖  */
  val southernHemisphereOpposition: Boolean = false,
  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR,

  val monthImpl: MonthImpl = MonthImpl.SolarTerms
) : java.io.Serializable {
  enum class MonthImpl {
    /** 標準, 節氣劃分月令 */
    SolarTerms,

    /** 120柱月令 */
    SunSign
  }
}

fun MonthConfig.MonthImpl.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      MonthConfig.MonthImpl.SolarTerms -> "傳統年月"
      MonthConfig.MonthImpl.SunSign    -> "120柱月令"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      MonthConfig.MonthImpl.SolarTerms -> "以「節氣」的「節」來切割月份"
      MonthConfig.MonthImpl.SunSign    -> "以節氣加星座 劃分月令：節氣的「節」與「氣」之間，屬於上一個星座，天干不變，地支退一位"
    }
  }
}

fun MonthConfig.MonthImpl.toString(locale: Locale): String {
  return this.asDescriptive().toString(locale)
}

fun MonthConfig.MonthImpl.getDescription(locale: Locale): String {
  return this.asDescriptive().getDescription(locale)
}

class MonthConfigBuilder : Builder<MonthConfig> {

  /** 南半球月令是否對沖  */
  var southernHemisphereOpposition: Boolean = false

  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  var hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR

  var monthImpl: MonthConfig.MonthImpl = MonthConfig.MonthImpl.SolarTerms

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
  val yearConfig: YearConfig = YearConfig(),
  val monthConfig: MonthConfig = MonthConfig()
) : java.io.Serializable

@DestinyMarker
class YearMonthConfigBuilder : Builder<YearMonthConfig> {

  private var yearConfig: YearConfig = YearConfig()

  fun year(block: YearConfigBuilder.() -> Unit) {
    this.yearConfig = YearConfigBuilder.yearConfig(block)
  }

  private var monthConfig: MonthConfig = MonthConfig()

  fun month(block: MonthConfigBuilder.() -> Unit) {
    this.monthConfig = MonthConfigBuilder.monthConfig(block)
  }

  override fun build(): YearMonthConfig {
    return YearMonthConfig(yearConfig, monthConfig)
  }

  companion object {
    fun yearMonthConfig(block: YearMonthConfigBuilder.() -> Unit = {}): YearMonthConfig {
      return YearMonthConfigBuilder().apply(block).build()
    }
  }
}

/**
 * 月干支
 */
@Named
class YearMonthFeature(private val starPositionImpl: IStarPosition<*>,
                       private val starTransitImpl: IStarTransit,
                       private val julDayResolver: JulDayResolver) : AbstractCachedFeature<YearMonthConfig, IStemBranch>() {

  override val key: String = "month"

  override val defaultConfig: YearMonthConfig = YearMonthConfig()

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl, julDayResolver)
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: YearMonthConfig): IStemBranch {
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
      MonthConfig.MonthImpl.SolarTerms -> originalMonth
      MonthConfig.MonthImpl.SunSign    -> {
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
