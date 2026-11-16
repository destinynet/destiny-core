/**
 * Created by smallufo on 2026-05-14.
 */
package destiny.core.astrology

/**
 * Compute Hellenistic Lots (Fortune, Spirit, Eros, Victory, Necessity,
 * Courage, Nemesis) for a horoscope. The day/night (sect) variant of each formula is chosen by the implementation.
 */
interface ILotCalculator {
  fun compute(model: IHoroscopeModel, v1Dto: IHoroscopeDto, horoConfig: IHoroscopeConfig): Map<Lot, LotInfo>
}
