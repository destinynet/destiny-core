/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import kotlinx.serialization.Serializable

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
 * 包夾的判準選項（同一套演算法的參數；不同的演算法是另一個 [IBesiegement] 實作）。預設值是 Porphyry 的 enclosure（[PORPHYRY]）。
 *
 * - [maxDistance]：兩側各自離被夾的星不超過幾度
 * - [byRays]：度數光線也算一側；false ＝ 只算星體本身
 * - [sameSign]：三顆星要在同一星座
 * - [maleficRays]／[beneficRays]：被凶星（火土）／其他星包夾時，光線算哪些交角。凶星預設只算四分、對分（舊判準的「只考量硬角度」；原典是否如此未查證）
 *
 * 介入（第三顆星落在中間就破除）一律以星體加托勒密四種光線判斷，不受這些選項影響。
 */
@Serializable
data class BesiegementOptions(
  val maxDistance: Double = 7.0,
  val byRays: Boolean = true,
  val sameSign: Boolean = false,
  val maleficRays: Set<Aspect> = setOf(Aspect.SQUARE, Aspect.OPPOSITION),
  val beneficRays: Set<Aspect> = PTOLEMAIC_RAYS,
) {
  /** 被 [enclosers] 包夾時，光線算哪些交角 */
  fun raysFor(enclosers: Set<Planet>): Set<Aspect> = when {
    !byRays                -> emptySet()
    enclosers == MALEFICS -> maleficRays
    else                   -> beneficRays
  }

  companion object {
    /** 托勒密相位的光線：六合、四分、三分、對分 */
    val PTOLEMAIC_RAYS: Set<Aspect> = setOf(Aspect.SEXTILE, Aspect.SQUARE, Aspect.TRINE, Aspect.OPPOSITION)

    private val MALEFICS: Set<Planet> = setOf(Planet.MARS, Planet.SATURN)

    /** Porphyry, *Introduction* 15：兩側各 7° 內、星體或光線、不要求同星座 */
    val PORPHYRY = BesiegementOptions()
  }
}

/**
 * 包夾。只看盤面時刻的位置，不需要星曆
 */
interface IBesiegement {

  /** [planet] 是否被 [enclosers]（兩顆星，例：火土）包夾；沒有則為 null */
  fun getBesiegement(h: IHoroscopeModel, planet: Planet, enclosers: Set<Planet>): Besiegement?

  /** 全盤：七顆古典行星被火土、被金木的包夾 */
  fun getResults(h: IHoroscopeModel): List<Besiegement> =
    listOf(setOf(Planet.MARS, Planet.SATURN), setOf(Planet.VENUS, Planet.JUPITER)).flatMap { enclosers ->
      Planet.classicalList.mapNotNull { getBesiegement(h, it, enclosers) }
    }
}
