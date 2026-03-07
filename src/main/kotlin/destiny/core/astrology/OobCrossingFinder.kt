/**
 * Created by smallufo on 2026-03-07.
 *
 * Finds the exact GMT times when a star's equatorial declination crosses
 * the obliquity threshold (±23.44°), i.e., enters or leaves Out of Bounds.
 *
 * Uses bisection on [IStarPosition] with [Coordinate.EQUATORIAL].
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay

data class OobCrossing(
  val star: Star,
  val gmtJulDay: GmtJulDay,
  /** true = entering OOB, false = returning in-bounds */
  val entering: Boolean,
  /** declination at the crossing moment (approx ±obliquity) */
  val declination: Double,
)

object OobCrossingFinder {

  /** Mean obliquity of the ecliptic (degrees) */
  const val OBLIQUITY = 23.44

  /** Sampling step in days — 1 day is sufficient for planets (Moon needs ~0.25) */
  private const val DEFAULT_STEP_DAYS = 1.0

  /** Bisection convergence tolerance in days (~1 minute) */
  private const val TOLERANCE_DAYS = 1.0 / 1440.0

  /**
   * Find all OOB ingress/egress events for [star] in the range [fromGmt]..[toGmt].
   *
   * Algorithm:
   * 1. Sample declination at regular intervals ([stepDays])
   * 2. Detect sign changes in `|decl| - obliquity` between consecutive samples
   * 3. Bisect each crossing to find the precise moment
   *
   * @param starPosition  Position calculator (SwissEph-backed)
   * @param star          The star to track
   * @param fromGmt       Range start (GMT Julian Day)
   * @param toGmt         Range end (GMT Julian Day)
   * @param options       Star type options (for LunarNode TRUE/MEAN)
   * @param obliquity     Threshold in degrees (default 23.44°)
   * @param stepDays      Sampling interval in days (default 1.0; use 0.25 for Moon)
   */
  fun findCrossings(
    starPosition: IStarPosition<*>,
    star: Star,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    options: StarTypeOptions = StarTypeOptions.MEAN,
    obliquity: Double = OBLIQUITY,
    stepDays: Double = DEFAULT_STEP_DAYS,
  ): Sequence<OobCrossing> {

    fun declAt(gmt: GmtJulDay): Double {
      return starPosition.calculate(star, gmt, Centric.GEO, Coordinate.EQUATORIAL, options).lat
    }

    /** signed distance: positive = OOB, negative = in-bounds */
    fun oobDistance(decl: Double): Double = kotlin.math.abs(decl) - obliquity

    return sequence {
      var t = fromGmt
      var prevDecl = declAt(t)
      var prevDist = oobDistance(prevDecl)

      while (t < toGmt) {
        val nextT = minOf(t + stepDays, toGmt)
        val nextDecl = declAt(nextT)
        val nextDist = oobDistance(nextDecl)

        // Sign change in oobDistance means a crossing happened
        if (prevDist * nextDist < 0) {
          // Bisect to find precise crossing
          var lo = t
          var hi = nextT
          while ((hi.value - lo.value) > TOLERANCE_DAYS) {
            val mid = GmtJulDay((lo.value + hi.value) / 2.0)
            val midDecl = declAt(mid)
            val midDist = oobDistance(midDecl)
            if (prevDist * midDist <= 0) {
              hi = mid
            } else {
              lo = mid
              prevDist = midDist
            }
          }

          val crossingTime = GmtJulDay((lo.value + hi.value) / 2.0)
          val crossingDecl = declAt(crossingTime)
          val entering = oobDistance(prevDecl) < 0 // was in-bounds, now OOB

          yield(OobCrossing(star, crossingTime, entering, crossingDecl))
        }

        t = nextT
        prevDecl = nextDecl
        prevDist = nextDist
      }
    }
  }
}
