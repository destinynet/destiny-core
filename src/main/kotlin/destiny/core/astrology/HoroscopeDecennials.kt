/**
 * 十年限（Decennials）的盤面入口。
 *
 * Created by smallufo on 2026-11-26.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.astrology.prediction.Decennial
import destiny.core.astrology.prediction.DecennialsSectLight
import destiny.core.astrology.prediction.IDecennialsStart
import destiny.core.astrology.prediction.ValensYearLength
import destiny.core.astrology.prediction.decennialsAt
import destiny.core.astrology.prediction.decennialsOrder
import destiny.core.astrology.prediction.generateDecennials
import destiny.core.calendar.GmtJulDay

/**
 * 這張盤十年限的七星輪替順序（起始星在第一個）。
 *
 * 順序只看七星的星座與度數，不需要宮位；晝夜由 [dayNight] 指定時（只知晝夜、不知時刻）不必知道上升，
 * 但月亮一天走約 13°，時刻不準時月亮與鄰星的先後可能對調。
 *
 * @param dayNight 見 [getFirdaria]：非 null 直接採用，null 由盤面太陽位置推導
 */
fun IHoroscopeModel.decennialsOrder(dayNight: DayNight? = null, start: IDecennialsStart = DecennialsSectLight): List<Planet> {
  val positions = Planet.classicalList.associateWith { planet ->
    getZodiacDegree(planet) ?: throw IllegalStateException("Cannot find position of $planet")
  }
  return decennialsOrder(start.starter(isDiurnal(dayNight), positions), positions)
}

/** 與 `[from, to)` 重疊的十年限段落（L1..[maxLevel]），見 [generateDecennials] */
fun IHoroscopeModel.getRangeDecennials(
  from: GmtJulDay,
  to: GmtJulDay,
  maxLevel: Int = 2,
  dayNight: DayNight? = null,
  start: IDecennialsStart = DecennialsSectLight,
  yearLength: ValensYearLength = ValensYearLength.DEFAULT,
): List<Decennial> = generateDecennials(decennialsOrder(dayNight, start), gmtJulDay, from, to, maxLevel, yearLength)

/** [gmt] 所在的各層十年限（外層在前），見 [decennialsAt] */
fun IHoroscopeModel.getDecennialsAt(
  gmt: GmtJulDay,
  maxLevel: Int = 3,
  dayNight: DayNight? = null,
  start: IDecennialsStart = DecennialsSectLight,
  yearLength: ValensYearLength = ValensYearLength.DEFAULT,
): List<Decennial> = decennialsAt(decennialsOrder(dayNight, start), gmtJulDay, gmt, maxLevel, yearLength)
