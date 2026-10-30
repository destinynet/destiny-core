/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 一次包夾（希臘化的 enclosure／emperischesis，中世紀的 besiegement）：[planet] 被 [before]、[after] 兩側夾住。
 * [before] 在黃經較小的一側，[after] 在較大的一側，兩側是不同的星（火土，或金木）。
 *
 * 每一側可以是星體本身（[Side.aspect] 為 null），或該星以 [Side.aspect] 投射到的度數光線；[Side.distance] 是與 [planet] 的度數差。
 */
data class Besiegement(
  val planet: Planet,
  val before: Side,
  val after: Side,
) : ClassicalFact {

  data class Side(val by: Planet, val aspect: Aspect?, val distance: Double)

  /** 兩側都是星體本身 */
  val byBody: Boolean get() = before.aspect == null && after.aspect == null

  val between: Set<Planet> get() = setOf(before.by, after.by)
}

/**
 * 包夾。只看盤面時刻的位置，不需要星曆
 */
interface IBesiegement {

  /**
   * [planet] 是否被 [enclosers]（兩顆星，例：火土）包夾；沒有則為 null。
   * [aspects]：光線算哪些交角（星體本身一律算）
   */
  fun getBesiegement(h: IHoroscopeModel, planet: Planet, enclosers: Set<Planet>, aspects: Set<Aspect> = PTOLEMAIC_RAYS): Besiegement?

  companion object {
    /** 托勒密相位的光線：六合、四分、三分、對分 */
    val PTOLEMAIC_RAYS: Set<Aspect> = setOf(Aspect.SEXTILE, Aspect.SQUARE, Aspect.TRINE, Aspect.OPPOSITION)
  }
}
