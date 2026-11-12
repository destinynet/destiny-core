/**
 * Created by smallufo on 2026-11-13.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.*

/**
 * Lot 的位置以公式（[lngOf]）現算，**不取** [IHoroscopeModel.positionMap] 裡的 Lot —— 一般盤的點位不含 Lot，
 * 而且日夜要跟呼叫端判定的那一份一致。上升取實際的上升點（[Axis.RISING]），盤上沒有就不出。
 */
class LotsImpl(
  private val rulerImpl: IRuler,
  val options: LotsOptions = LotsOptions.DEFAULT,
) : ILots {

  override fun getResults(h: IHoroscopeModel, sect: DayNight): List<ClassicalFact> {
    val lngOf = { p: AstroPoint -> h.positionMap[p]?.lngDeg }
    val ascSign = lngOf(Axis.RISING)?.sign ?: return emptyList()

    val placements: List<LotPlacement> = options.lots.mapNotNull { lot ->
      val lng = lot.lngOf(lngOf, sect, options.formula) ?: return@mapNotNull null
      val sign = lng.sign
      val lord = with(rulerImpl) { sign.getRulerPoint() } as? Planet ?: return@mapNotNull null
      val lordSign = lngOf(lord)?.sign ?: return@mapNotNull null
      val placeOf: Map<Planet, Int> = options.planets.mapNotNull { p -> lngOf(p)?.sign?.let { s -> p to s.placeFrom(sign) } }.toMap()
      val byPlace = options.places.sorted()
        .associateWith { place -> options.planets.filter { placeOf[it] == place } }
        .filterValues { it.isNotEmpty() }
      LotPlacement(
        lot = lot,
        lng = lng,
        operands = lot.operandsOf(sect, options.formula),
        house = sign.placeFrom(ascSign),
        lord = lord,
        lordPlace = lordSign.placeFrom(sign),
        lordAspect = lordSign.aspectTo(sign),
        planetsByPlace = byPlace,
      )
    }

    val pairs = placements.flatMapIndexed { i, a ->
      placements.drop(i + 1).map { b -> LotPair(a.lot, b.lot, a.lng.sign.aspectTo(b.lng.sign)) }
    }
    return placements + pairs
  }
}
