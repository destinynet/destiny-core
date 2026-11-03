/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 野生：與其餘每一顆古典行星，在 [FeralOptions.aspects] 的任何一種相位都不成立（[FeralOptions.basis]）。
 * 只看盤面時刻，不看之後會不會成形 —— 「離開星座前不再成形」是空亡（[IVoidCourse]），兩者分開
 */
class FeralImpl(
  val options: FeralOptions = FeralOptions.DEFAULT,
) : IFeral {

  override fun getFeral(h: IHoroscopeModel, planet: Planet): Feral? {
    if (planet !in options.planets) return null
    val deg = h.positionMap[planet]?.lngDeg ?: return null
    val others = (Planet.classicalList - planet).mapNotNull { o -> h.positionMap[o]?.lngDeg?.let { o to it } }
    if (others.isEmpty()) return null

    val aspected = when (options.basis) {
      FeralOptions.Basis.BY_ORB  -> others.any { (o, oDeg) ->
        options.aspects.any { a -> options.aspectEffective.isEffective(planet, deg, o, oDeg, a) }
      }
      FeralOptions.Basis.BY_SIGN -> {
        val places = options.aspects.flatMap { SIGN_PLACES[it].orEmpty() }.toSet()
        others.any { (_, oDeg) -> oDeg.sign.placeFrom(deg.sign) in places }
      }
    }
    return if (aspected) null else Feral(planet, options.basis)
  }

  override fun getResults(h: IHoroscopeModel): List<Feral> =
    options.planets.mapNotNull { getFeral(h, it) }

  companion object {
    /** 以星座計，各相位對應的星座位置（從自己的星座起算第幾個） */
    private val SIGN_PLACES: Map<Aspect, Set<Int>> = mapOf(
      Aspect.CONJUNCTION to setOf(1),
      Aspect.SEXTILE to setOf(3, 11),
      Aspect.SQUARE to setOf(4, 10),
      Aspect.TRINE to setOf(5, 9),
      Aspect.OPPOSITION to setOf(7),
    )
  }
}
