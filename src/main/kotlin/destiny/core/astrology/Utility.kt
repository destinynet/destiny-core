/**
 * Created by smallufo on 2026-05-15.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay

/**
 * Equatorial coordinates (RA, Dec) for a [Star] at [gmtJulDay], without requiring
 * an [IHoroscopeModel] — the model-based overload only ever used `gmtJulDay` and
 * `centric` for stars, and callers such as astro-geo (ACG) contexts operate at
 * arbitrary moments (e.g. transit time) where no chart exists yet.
 */
fun Star.toEquatorial(
  gmtJulDay: GmtJulDay,
  centric: Centric,
  starPosition: IStarPosition<IStarPos>,
  starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN,
): EquatorialPos? {
  return runCatching {
    val eq = starPosition.calculate(this, gmtJulDay, centric, Coordinate.EQUATORIAL, starTypeOptions)
    // swisseph EQUATORIAL convention: lng = RA, lat = Dec
    EquatorialPos(rightAscension = eq.lng, declination = eq.lat)
  }.getOrNull()
}

/**
 * Equatorial coordinates (RA, Dec) for an [AstroPoint] at the moment carried by [model].
 *
 *  - [Star]  : computed by [starPosition] in [Coordinate.EQUATORIAL].
 *  - [Axis]  : converted from ecliptic longitude (β=0) via [obliquity].
 *  - others  : `null`.
 *
 * The caller chooses [obliquity] accuracy — typically obtained from an
 * [IObliquityCalculator] (e.g. [Astronomical] for pure-math mean, or a
 * swisseph-backed impl for apparent obliquity).
 */
fun AstroPoint.toEquatorial(
  model: IHoroscopeModel,
  horoConfig: IHoroscopeConfig,
  starPosition: IStarPosition<IStarPos>,
  obliquity: Double,
): EquatorialPos? {
  return when (this) {
    is Star -> toEquatorial(model.gmtJulDay, model.centric, starPosition, horoConfig.starTypeOptions)

    is Axis -> {
      val eclLng = model.positionMap[this]?.lng ?: return null
      Astronomical.eclipticToEquatorial(eclLng, 0.0, obliquity)
    }

    else -> null
  }
}
