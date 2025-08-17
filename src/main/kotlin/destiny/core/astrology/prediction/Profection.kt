/**
 * Created by smallufo on 2025-08-17.
 */
package destiny.core.astrology.prediction

import destiny.core.Scale
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Profection(
  val scale: Scale,
  val lord: Planet,
  val ascSign: ZodiacSign,
  val house: Int,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
)
