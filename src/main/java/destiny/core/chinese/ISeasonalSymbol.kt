/**
 * Created by smallufo on 2019-03-02.
 */
package destiny.core.chinese

import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.iching.Symbol
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 取得某時刻，得令的卦象 [destiny.iching.Symbol]
 */
interface ISeasonalSymbol {
  fun getSeasonalSymbol(gmtJulDay: Double): Set<Symbol>

  fun getSeasonalSymbol(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Symbol> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getSeasonalSymbol(gmtJulDay)
  }
}

/**
 * 東方設定：四立點
 */
class SeasonalSymbolChineseImpl(val solarTermsImpl: ISolarTerms) : ISeasonalSymbol, Serializable {

  override fun getSeasonalSymbol(gmtJulDay: Double): Set<Symbol> {
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
            Branch.丑 -> Symbol.艮
            Branch.辰 -> Symbol.巽
            Branch.未 -> Symbol.坤
            Branch.戌 -> Symbol.乾
            else -> throw IllegalArgumentException("impossible : $branch")
          }
        }
      }
    }.let { s -> setOf(s) }
  }

}
