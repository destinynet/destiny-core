/**
 * Created by smallufo on 2026-05-15.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Right Ascension / Declination pair in degrees.
 *  - [rightAscension] is normalized to `[0, 360)`
 *  - [declination] is in `[-90, +90]`
 */
data class EquatorialPos(
  val rightAscension: Double,
  val declination: Double,
)

/**
 * Pure-math astronomical utilities used across calculators
 * (mundane / paran / primary directions / OOB ...).
 *
 * All functions are side-effect-free and have no swisseph dependency; for
 * swisseph-backed higher-precision obliquity (apparent, with nutation) see
 * [IObliquityCalculator].
 */
object Astronomical : IObliquityCalculator {

  /** J2000.0 epoch in Julian Day. */
  const val J2000 = 2451545.0
  /** Julian century length in days. */
  const val JULIAN_CENTURY = 36525.0

  /**
   * Mean obliquity of the ecliptic at [jd], in degrees.
   *
   * IAU 1980 / Meeus *Astronomical Algorithms* eq. 22.2. Accurate to
   * ~0.01° over `[−3000, +3000]`; for higher precision (apparent obliquity
   * including nutation) use [IObliquityCalculator].
   */
  override fun getObliquity(jd: GmtJulDay): Double {
    val t = (jd.value - J2000) / JULIAN_CENTURY
    val arcSec = 21.448 - 46.8150 * t - 0.00059 * t * t + 0.001813 * t * t * t
    return 23.0 + 26.0 / 60.0 + arcSec / 3600.0
  }

  /**
   * Mean Greenwich Sidereal Time at [jd], in hours `[0, 24)`.
   * Meeus eq. 12.4. Does not include nutation (use the apparent variant
   * if sub-arcsecond accuracy is needed).
   */
  fun gmst(jd: GmtJulDay): Double {
    val deltaJd = jd.value - J2000
    val t = deltaJd / JULIAN_CENTURY
    val deg = 280.46061837 +
              360.98564736629 * deltaJd +
              0.000387933 * t * t -
              t * t * t / 38710000.0
    return ((deg % 360.0 + 360.0) % 360.0) / 15.0
  }

  /**
   * Mean Local Sidereal Time at [jd] for the geographic longitude
   * [longitudeDeg] (east positive), in hours `[0, 24)`.
   */
  fun lmst(jd: GmtJulDay, longitudeDeg: Double): Double {
    val lst = (gmst(jd) + longitudeDeg / 15.0) % 24.0
    return if (lst < 0.0) lst + 24.0 else lst
  }

  /**
   * Convert ecliptic `(lng, lat)` in degrees to equatorial RA/Dec using
   * [obliquity] in degrees.
   *
   * Meeus eq. 13.3:
   * ```
   *   tan α = (sin λ cos ε − tan β sin ε) / cos λ
   *   sin δ =  sin β cos ε + cos β sin ε sin λ
   * ```
   */
  fun eclipticToEquatorial(lng: Double, lat: Double, obliquity: Double): EquatorialPos {
    val l = Math.toRadians(lng)
    val b = Math.toRadians(lat)
    val e = Math.toRadians(obliquity)
    val ra = atan2(sin(l) * cos(e) - tan(b) * sin(e), cos(l))
    val dec = asin((sin(b) * cos(e) + cos(b) * sin(e) * sin(l)).coerceIn(-1.0, 1.0))
    val raDeg = ((Math.toDegrees(ra) % 360.0) + 360.0) % 360.0
    return EquatorialPos(raDeg, Math.toDegrees(dec))
  }

  /**
   * Hour angle in hours `[-12, +12]`, positive = west of local meridian.
   *
   * @param lstHours Local sidereal time in hours
   * @param raDeg    Right ascension in degrees
   */
  fun hourAngle(lstHours: Double, raDeg: Double): Double =
    normalizeHours(lstHours - raDeg / 15.0)

  /** Normalize hours to `[-12, +12]`. */
  fun normalizeHours(h: Double): Double {
    var x = h % 24.0
    if (x > 12.0) x -= 24.0
    if (x < -12.0) x += 24.0
    return x
  }
}
