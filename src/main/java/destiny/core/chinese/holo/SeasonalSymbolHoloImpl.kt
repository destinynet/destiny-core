package destiny.core.chinese.holo

import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTerms.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ISeasonalSymbol
import destiny.iching.Symbol
import destiny.iching.Symbol.*
import java.io.Serializable
import kotlin.math.abs


/** 季月，化工 */
enum class EndSeasonSymbolSpan {
  FULL_MONTH, // 整月
  DAYS_18 // 月份「結尾」 18天
}


/**
 * 西方設定：二分二至 為季節的起點
 * @param endSeasonSymbolSpan 若為 [EndSeasonSymbolSpan.DAYS_18] , 每個「季月」「坤、艮」 「結束前」各旺 18天
 * 若其為 [EndSeasonSymbolSpan.FULL_MONTH] , 則 「季月」 的「坤、艮」旺全月
 *
 */
class SeasonalSymbolHoloImpl(val solarTermsImpl: ISolarTerms ,
                             private val endSeasonSymbolSpan: EndSeasonSymbolSpan) : ISeasonalSymbol, Serializable {
  override fun getSeasonalSymbol(gmtJulDay: Double): Set<Symbol> {

    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    return solarTerms.branch.takeIf { listOf(辰, 戌, 丑, 未).contains(it) }
      ?.let {
        solarTermsImpl.getMajorSolarTermsGmtBetween(gmtJulDay).second.second
      }?.takeIf {
        when (endSeasonSymbolSpan) {
          EndSeasonSymbolSpan.DAYS_18 -> abs(it-gmtJulDay) <= 18
          EndSeasonSymbolSpan.FULL_MONTH -> true
        }
      }
      ?.let { setOf(坤, 艮) }
      ?: {
        setOf(when (solarTerms) {
          冬至, 小寒, 大寒, 立春, 雨水, 驚蟄 -> 坎
          春分, 清明, 穀雨, 立夏, 小滿, 芒種 -> 震
          夏至, 小暑, 大暑, 立秋, 處暑, 白露 -> 離
          秋分, 寒露, 霜降, 立冬, 小雪, 大雪 -> 兌
        })
      }.invoke()
  }
}