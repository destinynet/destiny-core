/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Axis
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign

/**
 * 以星座計的不相見：落在對方星座起算的第 2、6、8、12 個星座（[Aversion.AVERSE_PLACES]）。只看盤面時刻的位置
 */
class AversionSignImpl(
  private val rulerImpl: IRuler,
  private val exaltImpl: IExaltation,
  val options: AversionOptions = AversionOptions.DEFAULT,
) : IAversion {

  override fun getResults(h: IHoroscopeModel): List<Aversion> {
    val signOf = { p: AstroPoint -> h.positionMap[p]?.lngDeg?.sign }

    /** [planet]（在 [sign]）與 [from]（在 [fromSign]）不相見，且沒有被 [AversionOptions.exceptions] 抵銷 */
    fun averse(planet: Planet, sign: ZodiacSign, from: AstroPoint, fromSign: ZodiacSign, basis: Aversion.Basis): Aversion? {
      val place = sign.placeFrom(fromSign).takeIf { it in Aversion.AVERSE_PLACES } ?: return null
      val linked = (SignLink.BEHOLDING in options.exceptions && sign.antiscionSign == fromSign) ||
        (SignLink.COMMANDING in options.exceptions && sign.contraAntiscionSign == fromSign)
      return if (linked) null else Aversion(planet, from, place, basis)
    }

    /** 各星與它所在星座的某一種主人 */
    fun byLord(basis: Aversion.Basis, lordOf: (ZodiacSign) -> AstroPoint?): List<Aversion> =
      if (basis !in options.bases) emptyList()
      else options.points.mapNotNull { p ->
        val sign = signOf(p) ?: return@mapNotNull null
        val lord = lordOf(sign) as? Planet ?: return@mapNotNull null
        if (lord === p) return@mapNotNull null
        val lordSign = signOf(lord) ?: return@mapNotNull null
        averse(p, sign, lord, lordSign, basis)
      }

    val byRuler = byLord(Aversion.Basis.RULER) { with(rulerImpl) { it.getRulerPoint() } }
    val byExaltation = byLord(Aversion.Basis.EXALTATION) { with(exaltImpl) { it.getExaltPoint() } }
    val byAscendant = if (Aversion.Basis.ASCENDANT !in options.bases) null else h.cuspDegreeMap[1]?.let { asc ->
      val ruler = h.ascendantRuler(rulerImpl) ?: return@let null
      val sign = signOf(ruler) ?: return@let null
      averse(ruler, sign, Axis.RISING, asc.sign, Aversion.Basis.ASCENDANT)
    }
    return byRuler + byExaltation + listOfNotNull(byAscendant)
  }
}
