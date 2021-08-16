/**
 * Created by smallufo on 2019-11-23.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.Scale
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTerms.*
import destiny.core.calendar.chinese.Yuan
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch
import destiny.core.fengshui.sanyuan.NineStar.*
import destiny.core.fengshui.sanyuan.NineStar.Companion.toStar
import destiny.core.iching.Symbol
import java.time.chrono.ChronoLocalDateTime


/**
 * 九星飛佈 年月日時
 */
interface INineStar {

  val yearMonthImpl: IYearMonth

  val dayHourImpl: IDayHour

  fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): List<NineStarModel>

  companion object {
    // ================ 年 ================

    /** 年紫白入中 */
    private fun getYearCenter(yuan: Yuan, year: StemBranch): Int {
      val steps = year.getAheadOf(StemBranch.甲子)
      return when (yuan) {
        Yuan.UP -> FlyingStar.getValue(1, steps, true)
        Yuan.MID -> FlyingStar.getValue(4, steps, true)
        Yuan.LOW -> FlyingStar.getValue(7, steps, true)
      }
    }

    /** 年紫白入中 */
    fun getYearCenterStar(yuan: Yuan, year: StemBranch): NineStar {
      return getYearCenter(yuan, year).toStar()
    }

    /**
     * 年紫白方位 (年星到方)
     * (固定順飛)
     * */
    fun getYearStar(center: NineStar, symbol: Symbol): NineStar {
      val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
      return NineStar.of(center.period + steps)
    }

    /**
     * 承上，傳回各方位的到方星 map
     */
    fun getYearStarMap(center: NineStar): Map<Symbol, NineStar> {
      return Symbol.values().associateWith { symbol -> getYearStar(center, symbol) }
    }

    /** 年紫白方位 : 元 + 年干支 + [Symbol] 方位 */
    private fun getYearStar(yuan: Yuan, year: StemBranch, symbol: Symbol): NineStar {
      val center = getYearCenterStar(yuan, year)
      return getYearStar(center, symbol)
    }

    /**
     * 承上 , 傳回 map
     */
    fun getYearStarMap(yuan: Yuan, year: StemBranch): Map<Symbol, NineStar> {
      return Symbol.values().associateWith { symbol -> getYearStar(yuan, year, symbol) }
    }

    // ================ 月 ================

    /** 月紫白入中 */
    fun getMonthCenterStar(year: Branch, month: Branch): NineStar {
      // 寅月 , 何星入中
      val month寅: NineStar = when {
        arrayOf(子, 午, 卯, 酉).contains(year) -> {
          左輔 // 8
        }
        arrayOf(辰, 戌, 丑, 未).contains(year) -> {
          廉貞 // 5
        }
        else -> {
          巨門 // 2
        }
      }

      val steps: Int = month.getAheadOf(寅)
      // 逆行
      return month寅.prev(steps)
    }

    /** 月紫白飛星到方 */
    fun getMonthStar(year: Branch, month: Branch, symbol: Symbol): NineStar {
      val center = getMonthCenterStar(year, month)
      val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
      return NineStar.of(center.period + steps)
    }

    /** 承上 , 傳回 map */
    fun getMonthStarMap(year: Branch, month: Branch): Map<Symbol, NineStar> {
      return Symbol.values().associateWith { symbol -> getMonthStar(year, month, symbol) }
    }

    // ================ 日 ================

    /**
     * 日紫白入中 , by 節氣
     */
    fun getDayCenterStar(solarTerms: SolarTerms, day: StemBranch): NineStar {
      val steps = day.getAheadOf(StemBranch.甲子)
      return when (solarTerms) {
        冬至, 小寒, 大寒, 立春 -> 貪狼.next(steps)  // 1
        雨水, 驚蟄, 春分, 清明 -> 破軍.next(steps)  // 7
        穀雨, 立夏, 小滿, 芒種 -> 文曲.next(steps)  // 4
        夏至, 小暑, 大暑, 立秋 -> 右弼.prev(steps)  // 9
        處暑, 白露, 秋分, 寒露 -> 祿存.prev(steps)  // 3
        霜降, 立冬, 小雪, 大雪 -> 武曲.prev(steps)  // 6
      }
    }

    /**
     * 日紫白入中 , by 黃經度數
     */
    fun getDayCenterStar(zodiacDegree: Double, day: StemBranch): NineStar {
      val solarTerms = SolarTerms.getFromDegree(zodiacDegree)
      return getDayCenterStar(solarTerms, day)
    }

    /**
     * 日紫白飛星到方
     */
    fun getDayStar(solarTerms: SolarTerms, day: StemBranch, symbol: Symbol): NineStar {
      val center = getDayCenterStar(solarTerms, day)
      val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
      return if (solarTerms.zodiacDegree >= 270 || solarTerms.zodiacDegree < 90) {
        // 冬至後 to 夏至前
        NineStar.of(center.period + steps)
      } else {
        // 夏至後 to 冬至前
        NineStar.of(center.period - steps)
      }
    }

    /** 承上 , 傳回 map */
    fun getDayStarMap(zodiacDegree: Double, day: StemBranch): Map<Symbol, NineStar> {
      val solarTerms = SolarTerms.getFromDegree(zodiacDegree)
      return Symbol.values().associateWith { symbol -> getDayStar(solarTerms, day, symbol) }
    }

    // ================ 時 ================
    /**
     * 時紫白入中
     */
    fun getHourCenterStar(zodiacDegree: Double, day: Branch, hour: Branch): NineStar {
      val steps = hour.getAheadOf(子)
      return if (zodiacDegree >= 270 || zodiacDegree < 90) {
        // 冬至後 to 夏至前
        when (day) {
          子, 午, 卯, 酉 -> 貪狼.next(steps)  // from 1
          辰, 戌, 丑, 未 -> 文曲.next(steps)  // from 4
          寅, 巳, 申, 亥 -> 破軍.next(steps)  // from 7
        }
      } else {
        // 夏至後 to 冬至前
        when (day) {
          子, 午, 卯, 酉 -> 右弼.prev(steps)  // from 9
          辰, 戌, 丑, 未 -> 武曲.prev(steps)  // from 6
          寅, 巳, 申, 亥 -> 祿存.prev(steps)  // from 3
        }
      }
    }

    /**
     * 時紫白飛星到方
     */
    private fun getHourStar(zodiacDegree: Double, day: Branch, hour: Branch, symbol: Symbol): NineStar {
      val center = getHourCenterStar(zodiacDegree, day, hour)
      val steps: Int = FlyingStar.symbolPeriods.indexOf(symbol)
      return if (zodiacDegree >= 270 || zodiacDegree < 90) {
        // 冬至後 to 夏至前
        NineStar.of(center.period + steps)
      } else {
        // 夏至後 to 冬至前
        NineStar.of(center.period - steps)
      }
    }

    /** 承上 , 傳回 map */
    fun getHourStarMap(zodiacDegree: Double, day: Branch, hour: Branch): Map<Symbol, NineStar> {
      return Symbol.values().associateWith { symbol -> getHourStar(zodiacDegree, day, hour, symbol) }
    }

  } // companion
}
