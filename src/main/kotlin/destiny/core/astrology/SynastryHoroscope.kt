/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.SynastryGrain
import destiny.core.astrology.prediction.ISynastryAspect
import destiny.core.astrology.prediction.MidPointFocalAspect
import java.io.Serializable

data class SynastryFocalAspect(val inner: AstroPoint, val outer: AstroPoint, val aspect: Aspect, val orb: Double, val involved: List<MidPointFocalAspect>)

class SynastryHoroscope(
  val mode: SynastryGrain,
  val inner: IPersonHoroscopeModel,
  val outer: IPersonHoroscopeModel,
  val progressedAspects: List<ISynastryAspect>,
  val midpointFocalAspects: Set<ISynastryAspect>
) : Serializable {

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
