/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree

/**
 * 燃燒路（via combusta）：[planet]（通常只看月亮）落在天秤後半到天蠍前半這一段黃道 —— 古典視為月亮受損的地方，卜卦常見。
 * [lngDeg]：它的黃經
 */
data class ViaCombusta(
  val planet: Planet,
  val lngDeg: ZodiacDegree,
) : ClassicalFact

/**
 * 燃燒路的範圍：黃經 [from]（含）到 [to]（不含），以及判哪些星（[planets]）
 */
data class ViaCombustaOptions(
  val from: Double = 195.0,
  val to: Double = 225.0,
  val planets: List<Planet> = listOf(Planet.MOON),
) {
  init {
    require(from < to) { "from 要小於 to：$from, $to" }
  }

  companion object {
    /** Lilly《Christian Astrology》：天秤 15° 到天蠍 15° —— 最常被引用 */
    val LILLY = ViaCombustaOptions()

    /** Al-Biruni：天秤 19°（太陽入弱）到天蠍 3°（月亮入弱） */
    val AL_BIRUNI = ViaCombustaOptions(from = 199.0, to = 213.0)
  }
}

/** 燃燒路 */
interface IViaCombusta {

  /** [planet] 在燃燒路上；不在（或不判這顆星）為 null */
  fun getViaCombusta(h: IHoroscopeModel, planet: Planet): ViaCombusta?

  fun getResults(h: IHoroscopeModel): List<ViaCombusta>
}

class ViaCombustaImpl(
  val options: ViaCombustaOptions = ViaCombustaOptions.LILLY,
) : IViaCombusta {

  override fun getViaCombusta(h: IHoroscopeModel, planet: Planet): ViaCombusta? {
    if (planet !in options.planets) return null
    val deg = h.positionMap[planet]?.lngDeg ?: return null
    return deg.takeIf { it.value >= options.from && it.value < options.to }?.let { ViaCombusta(planet, it) }
  }

  override fun getResults(h: IHoroscopeModel): List<ViaCombusta> =
    options.planets.mapNotNull { getViaCombusta(h, it) }
}
