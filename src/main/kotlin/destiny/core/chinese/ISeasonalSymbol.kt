/**
 * Created by smallufo on 2019-03-02.
 */
package destiny.core.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 取得某時刻，得令的卦象 [destiny.core.iching.Symbol]
 */
interface ISeasonalSymbol {
  fun getSeasonalSymbol(gmtJulDay: GmtJulDay): Set<Symbol>

  fun getSeasonalSymbol(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Symbol> {
    return getSeasonalSymbol(lmt.toGmtJulDay(loc))
  }
}

/**
 * 東方設定：四立點
 */
class SeasonalSymbolChineseImpl(val solarTermsImpl: ISolarTerms) : ISeasonalSymbol, Serializable {

  override fun getSeasonalSymbol(gmtJulDay: GmtJulDay): Set<Symbol> {
    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    val branch = solarTerms.branch

    return SimpleBranch[branch].let { simpleBranch ->
      when (simpleBranch.fiveElement) {
        水 -> 坎
        木 -> 震
        火 -> 離
        金 -> 兌
        土 -> {
          when (branch) {
            丑 -> 艮
            辰 -> 巽
            未 -> 坤
            戌 -> 乾
            else -> throw IllegalArgumentException("impossible : $branch")
          }
        }
      }
    }.let { s -> setOf(s) }
  }

}
