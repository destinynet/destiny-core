/**
 * Created by smallufo on 2026-05-15.
 */
package destiny.core.astrology

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
    is Star -> runCatching {
      val eq = starPosition.calculate(
        this, model.gmtJulDay, model.centric, Coordinate.EQUATORIAL, horoConfig.starTypeOptions
      )
      // swisseph EQUATORIAL convention: lng = RA, lat = Dec
      EquatorialPos(rightAscension = eq.lng, declination = eq.lat)
    }.getOrNull()

    is Axis -> {
      val eclLng = model.positionMap[this]?.lng ?: return null
      Astronomical.eclipticToEquatorial(eclLng, 0.0, obliquity)
    }

    else -> null
  }
}
