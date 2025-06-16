/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Serializable


@Serializable
data class AnnualReport(
  val year: Int,
  val solarReturn: IHoroscopeDto,
  val synastryAspects: Set<SynastryAspect>,
  val from: GmtJulDay,
  val to: GmtJulDay,
)
