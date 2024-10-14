/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.astrology.prediction.ISynastryAspect
import java.io.Serializable

enum class SynastryMode {
  BOTH_FULL,    // Both parties have full date and time
  FULL_TO_DATE, // Inner chart has full data, outer chart only has date
  DATE_TO_FULL, // Inner chart only has date, outer chart has full data
  BOTH_DATE     // Both parties only have date, no time
}

class SynastryModel(
  val model: SynastryMode,
  val innerGender : Gender,
  val outerGender : Gender,
  val progressedAspects: Set<ISynastryAspect>
): Serializable {

  val progressedAspectsByScore: List<ISynastryAspect>
    get() {
      return progressedAspects.asSequence().sortedByDescending { it.score }.toList()
    }
}
