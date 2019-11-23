/**
 * Created by smallufo on 2019-11-23.
 */
package destiny.fengshui.sanyuan

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch
import destiny.fengshui.sanyuan.NineStar.Companion.toStar
import destiny.iching.Symbol

object NineStarTools {

  // ================ 年 ================

  /** 年紫白入中 */
  fun getCenter(yuan: Yuan, year: StemBranch): Int {
    val steps = year.getAheadOf(StemBranch.甲子)
    return when (yuan) {
      Yuan.UP -> FlyingStar.getValue(1, steps, true)
      Yuan.MID -> FlyingStar.getValue(4, steps, true)
      Yuan.LOW -> FlyingStar.getValue(7, steps, true)
    }
  }

  /** 年紫白入中 */
  fun getCenterStar(yuan: Yuan, year: StemBranch): NineStar {
    return getCenter(yuan, year).toStar()
  }


  /** 年紫白方位 (年星到方) */
  fun getYearStar(center: NineStar, symbol: Symbol): NineStar {
    val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
    return NineStar.of(center.period + steps)
  }

  /**
   * 承上，傳回各方位的到方星 map
   */
  fun getYearStarMap(center: NineStar): Map<Symbol, NineStar> {
    return Symbol.values().map { symbol ->
      symbol to getYearStar(center, symbol)
    }.toMap()
  }

  /** 年紫白方位 : 元 + 年干支 + [Symbol] 方位 */
  fun getYearStar(yuan: Yuan, year: StemBranch, symbol: Symbol): NineStar {
    val center = getCenterStar(yuan, year)
    return getYearStar(center, symbol)
  }

  /**
   * 承上 , 傳回 map
   */
  fun getYearStarMap(yuan: Yuan, year: StemBranch): Map<Symbol, NineStar> {
    return Symbol.values().map { symbol ->
      symbol to getYearStar(yuan, year, symbol)
    }.toMap()
  }

  // ================ 月 ================

  /** 月紫白入中 */
  fun getCenterStar(year: Branch, month: Branch): NineStar {
    // 寅月 , 何星入中
    val month寅: NineStar = when {
      arrayOf(子, 午, 卯, 酉).contains(year) -> {
        NineStar.左輔 // 8
      }
      arrayOf(辰, 戌, 丑, 未).contains(year) -> {
        NineStar.廉貞 // 5
      }
      else -> {
        NineStar.巨門 // 2
      }
    }

    val steps: Int = month.getAheadOf(寅)
    // 逆行
    return month寅.prev(steps)
  }

  /** 月紫白飛星到方 */
  fun getMonthStar(year: Branch, month: Branch, symbol: Symbol): NineStar {
    val center = getCenterStar(year, month)
    val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
    return NineStar.of(center.period + steps)
  }

  /** 承上 , 傳回 map */
  fun getMonthStarMap(year: Branch, month: Branch): Map<Symbol, NineStar> {
    return Symbol.values().map { symbol ->
      symbol to getMonthStar(year, month, symbol)
    }.toMap()
  }
}
