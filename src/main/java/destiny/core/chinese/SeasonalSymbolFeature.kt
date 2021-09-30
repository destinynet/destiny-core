/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.core.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTerms.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.DAYS_18
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.FULL_MONTH
import destiny.core.iching.Symbol
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Named
import kotlin.math.abs

@Serializable
data class SeasonalSymbolConfig(val impl: Impl = Impl.Chinese) {

  @Serializable
  sealed class Impl {

    @Serializable
    @SerialName("Chinese")
    object Chinese : Impl()

    @Serializable
    @SerialName("Holo")
    data class Holo(val endSeasonSymbolSpan: EndSeasonSymbolSpan = DAYS_18) : Impl() {
      /** 季月，化工 */
      enum class EndSeasonSymbolSpan {
        FULL_MONTH, // 整月
        DAYS_18 // 月份「結尾」 18天
      }
    }
  }
}

@DestinyMarker
class SeasonalSymbolConfigBuilder : Builder<SeasonalSymbolConfig> {

  var impl: SeasonalSymbolConfig.Impl = SeasonalSymbolConfig.Impl.Chinese

  override fun build(): SeasonalSymbolConfig {
    return SeasonalSymbolConfig(impl)
  }

  companion object {
    fun seasonalSymbolConfig(block: SeasonalSymbolConfigBuilder.() -> Unit = {}): SeasonalSymbolConfig {
      return SeasonalSymbolConfigBuilder().apply(block).build()
    }
  }
}


@Named
class SeasonalSymbolFeature(val solarTermsImpl: ISolarTerms) : AbstractCachedFeature<SeasonalSymbolConfig, Set<Symbol>>() {

  override val key: String = "seasonalSymbolFeature"

  override val defaultConfig: SeasonalSymbolConfig = SeasonalSymbolConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: SeasonalSymbolConfig): Set<Symbol> {

    return when (config.impl) {
      is SeasonalSymbolConfig.Impl.Chinese -> chinese(gmtJulDay)
      is SeasonalSymbolConfig.Impl.Holo    -> holo(gmtJulDay, config.impl.endSeasonSymbolSpan)
    }
  }

  /**
   * 東方設定：四立點
   */
  private fun chinese(gmtJulDay: GmtJulDay): Set<Symbol> {
    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    val branch = solarTerms.branch

    return SimpleBranch[branch].let { simpleBranch ->
      when (simpleBranch.fiveElement) {
        FiveElement.水 -> Symbol.坎
        FiveElement.木 -> Symbol.震
        FiveElement.火 -> Symbol.離
        FiveElement.金 -> Symbol.兌
        FiveElement.土 -> {
          when (branch) {
            丑    -> Symbol.艮
            辰    -> Symbol.巽
            未    -> Symbol.坤
            戌    -> Symbol.乾
            else -> throw IllegalArgumentException("impossible : $branch")
          }
        }
      }
    }.let { s -> setOf(s) }
  }

  /**
   * 西方設定：二分二至 為季節的起點
   * @param endSeasonSymbolSpan
   * 若為 [DAYS_18] , 每個「季月」「坤、艮」 「結束前」各旺 18天
   * 若為 [FULL_MONTH] , 則 「季月」 的「坤、艮」旺全月
   */
  private fun holo(gmtJulDay: GmtJulDay, endSeasonSymbolSpan: SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan): Set<Symbol> {
    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    return solarTerms.branch.takeIf { listOf(辰, 戌, 丑, 未).contains(it) }
      ?.let {
        solarTermsImpl.getMajorSolarTermsGmtBetween(gmtJulDay).second.second
      }?.takeIf {
        when (endSeasonSymbolSpan) {
          DAYS_18    -> abs(it - gmtJulDay) <= 18
          FULL_MONTH -> true
        }
      }
      ?.let { setOf(Symbol.坤, Symbol.艮) }
      ?: setOf(
        when (solarTerms) {
          冬至, 小寒, 大寒, 立春, 雨水, 驚蟄 -> Symbol.坎
          春分, 清明, 穀雨, 立夏, 小滿, 芒種 -> Symbol.震
          夏至, 小暑, 大暑, 立秋, 處暑, 白露 -> Symbol.離
          秋分, 寒露, 霜降, 立冬, 小雪, 大雪 -> Symbol.兌
        }
      )
  }
}
