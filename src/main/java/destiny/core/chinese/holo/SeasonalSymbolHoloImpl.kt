package destiny.core.chinese.holo

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTerms.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ISeasonalSymbol
import destiny.core.chinese.SeasonalSymbolConfig
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*
import java.io.Serializable
import kotlin.math.abs

/**
 * 西方設定：二分二至 為季節的起點
 *
 * to be replaced by [destiny.core.chinese.SeasonalSymbolFeature]
 */
@Deprecated("SeasonalSymbolFeature")
class SeasonalSymbolHoloImpl(val solarTermsImpl: ISolarTerms ,
                             private val endSeasonSymbolSpan: SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan
) : ISeasonalSymbol, Serializable {
  override fun getSeasonalSymbol(gmtJulDay: GmtJulDay): Set<Symbol> {

    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    return solarTerms.branch.takeIf { listOf(辰, 戌, 丑, 未).contains(it) }
      ?.let {
        solarTermsImpl.getMajorSolarTermsGmtBetween(gmtJulDay).second.second
      }?.takeIf {
        when (endSeasonSymbolSpan) {
          SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.DAYS_18    -> abs(it-gmtJulDay) <= 18
          SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.FULL_MONTH -> true
        }
      }
      ?.let { setOf(坤, 艮) }
      ?: setOf(
        when (solarTerms) {
          冬至, 小寒, 大寒, 立春, 雨水, 驚蟄 -> 坎
          春分, 清明, 穀雨, 立夏, 小滿, 芒種 -> 震
          夏至, 小暑, 大暑, 立秋, 處暑, 白露 -> 離
          秋分, 寒露, 霜降, 立冬, 小雪, 大雪 -> 兌
        }
      )
  }
}
