/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.SynastryGrain
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SynastryFocalAspect(val inner: AstroPoint,
                               val outer: AstroPoint,
                               val aspect: Aspect,
                               @Serializable(with = DoubleTwoDecimalSerializer::class)
                               val orb: Double,
                               val involved: List<MidPointFocalAspect>)

class SynastryHoroscope(
  val mode: SynastryGrain,
  val inner: IPersonHoroscopeModel,
  val outer: IPersonHoroscopeModel,
  val progressedAspects: List<ISynastryAspect>,
  val midpointFocalAspects: Set<ISynastryAspect>,
  val houseOverlayMap: Map<Int, List<HouseOverlay>>
) {

  val progressedAspectsByScore: List<ISynastryAspect>
    get() {
      return progressedAspects.asSequence().sortedByDescending { it.score }.toList()
    }

  val synastryFocalAspects: List<SynastryFocalAspect>
    get() {
      return midpointFocalAspects.asSequence().map { it as MidPointFocalAspect }.groupBy { Triple(it.inner.focal, it.outer.focal, it.aspect) }
        .asSequence()
        .map { (triple: Triple<AstroPoint, AstroPoint, Aspect>, aspects: List<MidPointFocalAspect>) ->
          SynastryFocalAspect(triple.first, triple.second, triple.third, aspects.first().orb, aspects)
        }
        .sortedBy { it.orb }
        .toList()
    }
}

data class HouseOverlayRow(val point: AstroPoint, val inner: Int, val innerToOuter: Int, val outer : Int, val outerToInner: Int)


/**
 * outer 星體，映射到 inner natal 的第幾宮 , 以及距離宮首幾度
 */
@Serializable
data class HouseOverlay(
  val outerPoint: AstroPoint,
  val innerHouse: Int,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val degreeToCusp: Double,
)

@Serializable
data class Synastry(
  val aspects: List<SynastryAspect>,
  val houseOverlayMap: Map<Int, List<HouseOverlay>>
)
