/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.tools.CircleTools.normalizeSigned

/**
 * Porphyry 的 enclosure（*Introduction* 15，大量取自 Antiochus）：
 * - 兩側各是一顆包夾星的**星體**，或它投射到的**度數光線**（交角見 [BesiegementOptions.raysFor]）；一側星體、一側光線也算
 * - 兩側**各自**離被夾的星不超過 [BesiegementOptions.maxDistance] 度（Porphyry 的 7° 是對光線講的，星體套用同一個上限）
 * - 中間不能有別的星：任何第三顆古典行星的星體或光線落在被夾的星與任一側之間，包夾就破了（mesembolesis，介入）
 * - 預設不要求同星座（[BesiegementOptions.sameSign]）
 *
 * 2026-10-31 取代 2017 年的舊版（依時間上前一次、後一次成形的交角判斷包夾）。舊版的問題：
 * 不看容許度、不限距離，前一次成形的相位可能是幾個月前的事
 */
class BesiegementPorphyryImpl(
  private val options: BesiegementOptions = BesiegementOptions.PORPHYRY,
) : IBesiegement {

  /** [p] 的星體（aspect = null）與以 [aspects] 投射的光線落點 */
  private fun points(h: IHoroscopeModel, p: Planet, aspects: Set<Aspect>): List<Pair<Aspect?, Double>> {
    val lng = h.getPosition(p)?.lng ?: return emptyList()
    return listOf<Pair<Aspect?, Double>>(null to lng) + aspects.flatMap { a ->
      if (a.degree == 180.0) listOf(a to lng + 180) else listOf(a to lng + a.degree, a to lng - a.degree)
    }
  }

  override fun getBesiegement(h: IHoroscopeModel, planet: Planet, enclosers: Set<Planet>): Besiegement? {
    if (enclosers.size != 2 || planet in enclosers) return null
    val lng = h.getPosition(planet)?.lng ?: return null
    val aspects = options.raysFor(enclosers)
    val sign = ZodiacSign.of(lng)

    /** [by] 在 [planet] 某一側（[forward]：黃經較大的一側）最近的落點 */
    fun nearest(by: Planet, forward: Boolean): Besiegement.Side? =
      points(h, by, aspects).mapNotNull { (aspect, at) ->
        val d = (at - lng).normalizeSigned()
        val distance = if (forward) d else -d
        if (distance > 0 && distance <= options.maxDistance && (!options.sameSign || ZodiacSign.of(at) == sign))
          Besiegement.Side(by, aspect, distance)
        else null
      }.minByOrNull { it.distance }

    val (e1, e2) = enclosers.toList()
    val candidates = listOfNotNull(
      nearest(e1, false)?.let { b -> nearest(e2, true)?.let { a -> Besiegement(planet, b, a) } },
      nearest(e2, false)?.let { b -> nearest(e1, true)?.let { a -> Besiegement(planet, b, a) } },
    )

    // 介入：第三顆星的星體或光線（托勒密相位，不論包夾用的是哪幾種）落在被夾的星與任一側之間
    val others = Planet.classicalList.filter { it !== planet && it !in enclosers }
    return candidates.filter { c ->
      others.none { o ->
        points(h, o, BesiegementOptions.PTOLEMAIC_RAYS).any { (_, at) ->
          val d = (at - lng).normalizeSigned()
          (d < 0 && -d < c.before.distance) || (d > 0 && d < c.after.distance)
        }
      }
    }.minByOrNull { it.before.distance + it.after.distance }
  }
}
