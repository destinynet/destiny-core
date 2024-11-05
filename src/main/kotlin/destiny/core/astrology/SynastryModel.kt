/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.ISynastryAspect
import destiny.core.astrology.prediction.MidPointFocalAspect
import java.io.Serializable

enum class SynastryMode {
  BOTH_FULL,              // Both parties have full date and time
  INNER_FULL_OUTER_DATE,  // Inner chart has full data, outer chart only has date
  INNER_DATE_OUTER_FULL,  // Inner chart only has date, outer chart has full data
  BOTH_DATE               // Both parties only have date, no time
}

enum class SynastryDomain {
  OVERVIEW,
  EMOTIONAL,
  FINANCIAL,
  COMMUNICATION,
  PHYSICAL,
  GROWTH,
}

data class SynastryFocalAspect(val inner: AstroPoint, val outer: AstroPoint, val aspect: Aspect, val orb: Double, val involved: List<MidPointFocalAspect>)

class SynastryModel(
  val mode: SynastryMode,
  val inner: IPersonHoroscopeModel,
  val outer: IPersonHoroscopeModel,
  val progressedAspects: Set<ISynastryAspect>,
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
