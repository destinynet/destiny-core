/**
 * Extension functions on [IHoroscopeModel] for Zodiacal Releasing (黃道釋放法).
 *
 * Created by smallufo on 2026-03-11.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.ZodiacalReleasing
import destiny.core.astrology.prediction.generateZodiacalReleasing
import destiny.core.calendar.GmtJulDay

/**
 * Generate Zodiacal Releasing periods from a given Lot (Fortune or Spirit).
 *
 * @param lot the Arabic Lot to use as the starting point (typically [Arabic.Fortune] or [Arabic.Spirit])
 * @param endTime how far into the future to calculate
 * @param maxLevel maximum depth (1 = L1 only, 2 = L1+L2, etc.)
 * @return flat list of all periods, or null if the Lot's position is not found in this chart
 */
fun IHoroscopeModel.getZodiacalReleasing(
  lot: Arabic,
  endTime: GmtJulDay,
  maxLevel: Int = 2
): List<ZodiacalReleasing>? {
  val lotSign = getZodiacSign(lot) ?: return null
  return generateZodiacalReleasing(lotSign, gmtJulDay, endTime, maxLevel)
}

/**
 * Generate Zodiacal Releasing periods within a specific time range.
 * Only returns periods that overlap with [fromTime]..[toTime].
 *
 * @param lot the Arabic Lot to use as the starting point
 * @param fromTime start of the query range
 * @param toTime end of the query range
 * @param maxLevel maximum depth (1 = L1 only, 2 = L1+L2, etc.)
 * @return filtered list of periods overlapping the range, or null if the Lot's position is not found
 */
fun IHoroscopeModel.getRangeZodiacalReleasing(
  lot: Arabic,
  fromTime: GmtJulDay,
  toTime: GmtJulDay,
  maxLevel: Int = 2
): List<ZodiacalReleasing>? {
  val lotSign = getZodiacSign(lot) ?: return null
  return generateZodiacalReleasing(lotSign, gmtJulDay, toTime, maxLevel)
    .filter { it.fromTime < toTime && fromTime < it.toTime }
}
