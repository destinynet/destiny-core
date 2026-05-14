/**
 * Created by smallufo on 2026-05-14.
 */
package destiny.core.astrology

/**
 * Compute Hellenistic Arabic Lots (Fortune, Spirit, Eros, Victory, Necessity,
 * Courage, Nemesis) for a horoscope.
 *
 * Wraps destiny-core-impl's existing `posFortune` / `posSpirit` / ... factories
 * (see PositionFunctions.kt) — handles day/night sect formula switching automatically.
 */
interface IArabicLotCalculator {
  fun compute(model: IHoroscopeModel, v1Dto: IHoroscopeDto, horoConfig: IHoroscopeConfig): Map<Arabic, ArabicLotInfo>
}
