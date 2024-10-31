/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.ISynastryAspect
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

class SynastryModel(
  val mode: SynastryMode,
  val inner: IPersonHoroscopeModel,
  val outer: IPersonHoroscopeModel,
  val progressedAspects: Set<ISynastryAspect>
) : Serializable {

  val progressedAspectsByScore: List<ISynastryAspect>
    get() {
      return progressedAspects.asSequence().sortedByDescending { it.score }.toList()
    }
}
