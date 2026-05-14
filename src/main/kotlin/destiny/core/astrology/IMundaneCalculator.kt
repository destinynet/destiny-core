/**
 * Created by smallufo on 2026-05-14.
 */
package destiny.core.astrology

/**
 * Compute mundane (horizon-based) positions for all relevant points in a horoscope.
 *
 * Azimuth uses N=0 / E=90 / S=180 / W=270 convention (post-normalize, matches
 * destiny-core [IAzimuthCalculator] output).
 */
interface IMundaneCalculator {
  fun compute(model: IHoroscopeModel, v1Dto: IHoroscopeDto, horoConfig: IHoroscopeConfig): Map<AstroPoint, MundanePosition>
}
