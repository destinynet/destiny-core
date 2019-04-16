/**
 * Created by smallufo on 2019-02-27.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.SolarTerms.*
import destiny.core.calendar.SolarTermsTimePos
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.*
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import java.io.Serializable

/**
 * 四節氣卦
 */
data class SeasonalHexagram(
  /** 只會有 [Hexagram.坎] [Hexagram.離] [Hexagram.震] [Hexagram.兌] 這四卦 */
  val hex: IHexagram,
  /** 1 ~ 6 爻 */
  val line: Int,
  val solarTerms: SolarTerms
)

/** 四節氣卦 , 出自 孟喜 的卦氣說 */
val seasonHexMap: Map<SolarTerms, Pair<Symbol, Int>> = mapOf(
  冬至 to (Symbol.坎 to 1),
  小寒 to (Symbol.坎 to 2),
  大寒 to (Symbol.坎 to 3),
  立春 to (Symbol.坎 to 4),
  雨水 to (Symbol.坎 to 5),
  驚蟄 to (Symbol.坎 to 6),

  春分 to (Symbol.震 to 1),
  清明 to (Symbol.震 to 2),
  穀雨 to (Symbol.震 to 3),
  立夏 to (Symbol.震 to 4),
  小滿 to (Symbol.震 to 5),
  芒種 to (Symbol.震 to 6),

  夏至 to (Symbol.離 to 1),
  小暑 to (Symbol.離 to 2),
  大暑 to (Symbol.離 to 3),
  立秋 to (Symbol.離 to 4),
  處暑 to (Symbol.離 to 5),
  白露 to (Symbol.離 to 6),

  秋分 to (Symbol.兌 to 1),
  寒露 to (Symbol.兌 to 2),
  霜降 to (Symbol.兌 to 3),
  立冬 to (Symbol.兌 to 4),
  小雪 to (Symbol.兌 to 5),
  大雪 to (Symbol.兌 to 6)
)

/** 金鎖銀匙 參評歌訣 */
sealed class GoldenKey(open val code: Int,
                       override val fiveElement: FiveElement,
                       open val text: String,
                       open val day:Branch,
                       open val hour:Branch) : IFiveElement , Serializable {
  data class GoldenKeyGender(override val code: Int, override val fiveElement: FiveElement, val gender: Gender, override val text: String, override val day: Branch, override val hour: Branch) : GoldenKey(code, fiveElement, text , day, hour)
  data class GoldenKeyFlow(override val code: Int, override val fiveElement: FiveElement, override val text: String, override val day: Branch, override val hour: Branch) : GoldenKey(code, fiveElement, text , day, hour)
}


interface IHolo : IBirthDataNamePlace {

  val ew: IEightWords

  val yuan: Yuan

  /** 距離「節」與「氣」的時間 */
  val solarTermsPos: SolarTermsTimePos

  /** 天數 */
  val heavenNumber: Int

  val heavenSymbol: Symbol

  /** 地數 */
  val earthNumber: Int

  val earthSymbol: Symbol

  /** 先天卦 , with 元堂 (1~6) , 包含六爻中每年的流年資訊 , 6 or 9 年 */
  val hexagramCongenital: ILifeHoloHexagram

  /** 後天卦 , with 元堂 (1~6) , 包含六爻中每年的流年資訊 , 6 or 9 年 */
  val hexagramAcquired: ILifeHoloHexagram

  /** 天元氣 (from 年干) */
  val vigorousSymbolFromStem: Symbol

  /** 地元氣 (from 年支) */
  val vigorousSymbolFromBranch: Symbol

  /** 天元氣相反 */
  val vigorlessSymbolFromStem: Symbol

  /** 地元氣相反 */
  val vigorlessSymbolFromBranch: Symbol

  /**
   * 化工 : 得到季節力量者 , 這裡的季節，與中國的季節不同（遲了一個半月），反而與西洋的季節定義相同 : 二分二至 為 春夏秋冬的起點
   * 另外考慮了季月 艮坤 旺 18日
   */
  val seasonalSymbols: Set<Symbol>

  /** 化工反例 */
  val seasonlessSymbols: Set<Symbol>

  /** 四節氣卦 , 其中的 [Hexagram] 只會有 [Hexagram.坎] , [Hexagram.離] , [Hexagram.震] , [Hexagram.兌] 四種可能 */
  val seasonalHexagram: Pair<Hexagram, Int>

  /** 12消息卦 , from [IMonthlyHexagram] */
  val monthlyHexagram: Hexagram

  /** 60值日卦 , from [IDailyHexagram] */
  val dailyHexagram: Hexagram

  /** 金鎖銀匙 參評歌訣 */
  val goldenKey : GoldenKey?
}

data class Holo(
  val birthData: IBirthDataNamePlace,
  override val ew: IEightWords,
  override val gender: Gender,
  override val yuan: Yuan,
  override val solarTermsPos: SolarTermsTimePos,
  override val heavenNumber: Int,
  override val heavenSymbol: Symbol,
  override val earthNumber: Int,
  override val earthSymbol: Symbol,
  override val hexagramCongenital: ILifeHoloHexagram,
  override val hexagramAcquired: ILifeHoloHexagram,
  override val vigorousSymbolFromStem: Symbol,
  override val vigorousSymbolFromBranch: Symbol,
  override val vigorlessSymbolFromStem: Symbol,
  override val vigorlessSymbolFromBranch: Symbol,
  override val seasonalSymbols: Set<Symbol>,
  override val seasonlessSymbols: Set<Symbol>,
  override val seasonalHexagram: Pair<Hexagram, Int>,
  override val monthlyHexagram: Hexagram,
  override val dailyHexagram: Hexagram,
  override val goldenKey: GoldenKey?) : IHolo, IBirthDataNamePlace by birthData


