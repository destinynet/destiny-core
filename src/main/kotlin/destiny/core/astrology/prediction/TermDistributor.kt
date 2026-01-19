/**
 * Created by smallufo on 2026-01-19.
 * Distributor through the Bounds (界限主星)
 *
 * A Hellenistic timing technique that tracks planetary periods based on the
 * significator's (usually ASC) progression through zodiacal terms/bounds.
 *
 * Standard rate: 1 degree = 1 year
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.ZodiacDegree
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * A single period in the Term Distributor timeline.
 *
 * @param ruler The planet ruling this term/bound period
 * @param fromDegree The starting zodiacal degree of this period
 * @param toDegree The ending zodiacal degree of this period
 * @param fromTime The starting time (GMT Julian Day)
 * @param toTime The ending time (GMT Julian Day)
 */
@Serializable
data class TermDistributorPeriod(
  val ruler: AstroPoint,
  @Contextual
  val fromDegree: ZodiacDegree,
  @Contextual
  val toDegree: ZodiacDegree,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
) {
  /** Duration of this period in years (based on degree span) */
  val degreeSpan: Double
    get() = fromDegree.getAngle(toDegree)
}

/**
 * Complete Term Distributor timeline from a significator point.
 *
 * @param significator The starting point (usually ASC or MC)
 * @param startDegree The initial degree of the significator
 * @param periods List of term distributor periods
 */
@Serializable
data class TermDistributorTimeline(
  val significator: AstroPoint,
  @Contextual
  val startDegree: ZodiacDegree,
  val periods: List<TermDistributorPeriod>
) {
  /**
   * Find the term distributor ruler at a specific time.
   */
  fun getRulerAt(gmtJulDay: GmtJulDay): TermDistributorPeriod? {
    return periods.find { gmtJulDay in it.fromTime..it.toTime }
  }

  /**
   * Get all periods that overlap with the given time range.
   */
  fun getPeriodsBetween(from: GmtJulDay, to: GmtJulDay): List<TermDistributorPeriod> {
    return periods.filter { it.fromTime <= to && from <= it.toTime }
  }
}
