/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 行星離太陽太近（combustion，灼傷）：[planet] 與太陽的黃經距離 [distance] 落在 [zone] 這一圈。
 *
 * - [Zone.CAZIMI]（日心）：貼在太陽正中，古典反而視為大吉 —— 坐在國王的心上
 * - [Zone.COMBUST]（灼傷）：被太陽的光燒著，力量與能見度都受損
 * - [Zone.UNDER_BEAMS]（在太陽光束下）：還看不見，程度比灼傷輕
 *
 * [approaching]：正往太陽靠近（相對速度使兩者距離縮小）為 true，正在遠離為 false，速度不明為 null。
 * Lilly：靠近灼傷比離開灼傷更糟。
 * [limits]：判定時用的三圈界線（[CombustionOptions]）—— 畫面要照它畫出三圈，不另外寫死一份數字
 */
data class Combustion(
  val planet: Planet,
  val zone: Zone,
  val distance: Double,
  val approaching: Boolean?,
  val limits: Limits,
) : ClassicalFact {
  enum class Zone { CAZIMI, COMBUST, UNDER_BEAMS }

  /** 三圈的外緣（與太陽的黃經距離，度） */
  data class Limits(val cazimi: Double, val combust: Double, val underBeams: Double)
}

/**
 * 灼傷的判準選項（同一套演算法的參數）。距離一律是與太陽的黃經距離（度）。
 *
 * - [cazimi]：日心的範圍（嚴格小於）
 * - [combust]：灼傷的外緣（含）
 * - [underBeams]：光束下的外緣（含）
 * - [planets]：判哪些星（太陽本身不判）
 */
data class CombustionOptions(
  val cazimi: Double = 17.0 / 60.0,
  val combust: Double = 8.5,
  val underBeams: Double = 17.0,
  val planets: List<Planet> = Planet.classicalList - Planet.SUN,
) {
  val limits: Combustion.Limits get() = Combustion.Limits(cazimi, combust, underBeams)

  companion object {
    /** Lilly《Christian Astrology》：日心 17 角分內、灼傷 8.5° 內、光束下 17° 內 —— 最常被引用的一組數字 */
    val LILLY = CombustionOptions()
  }
}

/**
 * 灼傷。實作決定判法（目前只有以黃經距離分三圈的 [CombustionImpl]；以星座為界、或依星的光度給不同容許度，可另寫實作）
 */
interface ICombustion : IClassicalFactSource {

  /** [planet] 落在哪一圈；不在任何一圈（離太陽夠遠）為 null */
  fun getCombustion(h: IHoroscopeModel, planet: Planet): Combustion?

  /** 全盤：依距離由近到遠 */
  fun getResults(h: IHoroscopeModel): List<Combustion>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)
}
