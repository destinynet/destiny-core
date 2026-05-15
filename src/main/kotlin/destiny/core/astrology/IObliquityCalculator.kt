/**
 * Created by smallufo on 2026-05-15.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay

/**
 * Computes the obliquity of the ecliptic for a given Julian Day.
 *
 * The contract intentionally does not specify mean vs. apparent — implementations
 * decide. Known implementations:
 *  - [Astronomical] : pure-math mean obliquity (IAU 1980, ~0.01° precision)
 *  - `ObliquityCalculatorImpl` (in destiny-core-impl) : swisseph-backed apparent
 *    obliquity, includes nutation
 */
interface IObliquityCalculator {
  /** Obliquity of the ecliptic at [jd], in degrees. */
  fun getObliquity(jd: GmtJulDay): Double
}
