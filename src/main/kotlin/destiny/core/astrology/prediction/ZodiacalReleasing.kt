/**
 * Zodiacal Releasing (黃道釋放法)
 *
 * A Hellenistic time-lord technique from Vettius Valens (2nd century CE).
 * Starting from the sign of a Lot (typically Lot of Fortune or Lot of Spirit),
 * time is divided into periods based on the planetary years of each zodiac sign.
 *
 * Uses Egyptian years (1 year = 360 days, 1 month = 30 days).
 *
 * Each level subdivides by 12:
 *   L1 = sign_years × 360 days
 *   L2 = sign_years × 30 days
 *   L3 = sign_years × 2.5 days
 *   L4 = sign_years × 5 hours
 *   ...
 *
 * Loosing of the Bond (LB): When a sub-period completes a full 12-sign cycle
 * and there is remaining time, it jumps to the 7th sign from the starting sign
 * and continues until the parent period's time is exhausted.
 *
 * Created by smallufo on 2026-03-11.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.classical.AbstractPtolemy
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.math.pow

/** Planetary years assigned to each zodiac sign (from Vettius Valens) */
val zodiacalReleasingYears: Map<ZodiacSign, Int> = mapOf(
  ZodiacSign.ARIES to 15,
  ZodiacSign.TAURUS to 8,
  ZodiacSign.GEMINI to 20,
  ZodiacSign.CANCER to 25,
  ZodiacSign.LEO to 19,
  ZodiacSign.VIRGO to 20,
  ZodiacSign.LIBRA to 8,
  ZodiacSign.SCORPIO to 15,
  ZodiacSign.SAGITTARIUS to 12,
  ZodiacSign.CAPRICORN to 27,
  ZodiacSign.AQUARIUS to 30,
  ZodiacSign.PISCES to 12
)

/** Sum of all planetary years = one full L1 cycle */
val ZODIACAL_RELEASING_TOTAL_YEARS: Int = zodiacalReleasingYears.values.sum() // 211

/** Egyptian year = 360 days */
const val EGYPTIAN_YEAR_DAYS: Double = 360.0

/** Egyptian month = 30 days */
const val EGYPTIAN_MONTH_DAYS: Double = 30.0

/**
 * Duration unit (in days) for each level.
 * L1: 360 days per year
 * L2: 30 days per year (360/12)
 * L3: 2.5 days per year (30/12)
 * L4: ~0.2083 days per year (2.5/12)
 */
fun levelUnitDays(level: Int): Double = EGYPTIAN_YEAR_DAYS / 12.0.pow(level - 1)

@Serializable
data class ZodiacalReleasing(
  val level: Int,
  val sign: ZodiacSign,
  val lord: Planet,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay,
  /** Whether this period is in the Loosing of the Bond phase */
  val isLoosingOfBond: Boolean = false
)

/**
 * Get the traditional (Ptolemaic) domicile ruler of a zodiac sign.
 */
private fun rulerOf(sign: ZodiacSign): Planet = AbstractPtolemy.rulerMap.getValue(sign)

/**
 * Generate L1 periods from the Lot's sign.
 *
 * L1 simply cycles through the zodiac starting from [lotSign],
 * each sign lasting its planetary years × 360 days.
 * In practice, one full cycle = 211 years, so LB never triggers at L1
 * within a human lifetime.
 *
 * @param lotSign the zodiac sign where the Lot (Fortune/Spirit) falls
 * @param startTime birth time (GMT Julian Day)
 * @param endTime how far into the future to calculate
 */
fun generateL1(lotSign: ZodiacSign, startTime: GmtJulDay, endTime: GmtJulDay): List<ZodiacalReleasing> {
  val periods = mutableListOf<ZodiacalReleasing>()
  var currentTime = startTime
  var sign = lotSign

  while (currentTime < endTime) {
    val duration = zodiacalReleasingYears.getValue(sign) * EGYPTIAN_YEAR_DAYS
    val periodEnd = GmtJulDay(min(currentTime.value + duration, endTime.value))
    periods.add(ZodiacalReleasing(1, sign, rulerOf(sign), currentTime, periodEnd))
    currentTime = GmtJulDay(currentTime.value + duration)
    sign = sign.next(1)
  }

  return periods
}

/**
 * Generate sub-periods within a parent period.
 *
 * Starting from [parentSign], cycles through all 12 signs at the given [level].
 * If the first 12-sign pass doesn't exhaust the parent's duration,
 * Loosing of the Bond triggers: jump to the 7th sign from [parentSign]
 * and continue until time runs out.
 *
 * @param parentSign the zodiac sign of the parent period
 * @param parentFrom start time of the parent period
 * @param parentDuration total duration of the parent period in days
 * @param level the sub-period level (2, 3, 4, ...)
 */
fun generateSubPeriods(
  parentSign: ZodiacSign,
  parentFrom: GmtJulDay,
  parentDuration: Double,
  level: Int
): List<ZodiacalReleasing> {
  val unitDays = levelUnitDays(level)
  val periods = mutableListOf<ZodiacalReleasing>()
  var currentTime = parentFrom
  var remaining = parentDuration

  // First pass: 12 signs starting from parentSign
  var sign = parentSign
  for (i in 0 until 12) {
    if (remaining <= 0.0001) break // floating point tolerance
    val signDuration = zodiacalReleasingYears.getValue(sign) * unitDays
    val actualDuration = min(signDuration, remaining)
    val endTime = GmtJulDay(currentTime.value + actualDuration)
    periods.add(ZodiacalReleasing(level, sign, rulerOf(sign), currentTime, endTime))
    currentTime = endTime
    remaining -= actualDuration
    sign = sign.next(1)
  }

  // Loosing of the Bond: jump to 7th sign from parentSign
  if (remaining > 0.0001) {
    sign = parentSign.next(6) // 7th sign (e.g., Cancer if starting from Capricorn)
    while (remaining > 0.0001) {
      val signDuration = zodiacalReleasingYears.getValue(sign) * unitDays
      val actualDuration = min(signDuration, remaining)
      val endTime = GmtJulDay(currentTime.value + actualDuration)
      periods.add(ZodiacalReleasing(level, sign, rulerOf(sign), currentTime, endTime, isLoosingOfBond = true))
      currentTime = endTime
      remaining -= actualDuration
      sign = sign.next(1)
    }
  }

  return periods
}

/**
 * Generate a complete Zodiacal Releasing timeline up to the specified depth.
 *
 * @param lotSign the zodiac sign where the Lot falls
 * @param startTime birth time
 * @param endTime how far to calculate
 * @param maxLevel maximum depth (1 = L1 only, 2 = L1+L2, etc.)
 * @return flat list of all periods across all levels, sorted by fromTime then level
 */
fun generateZodiacalReleasing(
  lotSign: ZodiacSign,
  startTime: GmtJulDay,
  endTime: GmtJulDay,
  maxLevel: Int = 2
): List<ZodiacalReleasing> {
  val allPeriods = mutableListOf<ZodiacalReleasing>()

  val l1Periods = generateL1(lotSign, startTime, endTime)
  allPeriods.addAll(l1Periods)

  if (maxLevel >= 2) {
    for (l1 in l1Periods) {
      val l1Duration = l1.toTime.value - l1.fromTime.value
      val l2Periods = generateSubPeriods(l1.sign, l1.fromTime, l1Duration, 2)
      allPeriods.addAll(l2Periods)

      if (maxLevel >= 3) {
        for (l2 in l2Periods) {
          val l2Duration = l2.toTime.value - l2.fromTime.value
          val l3Periods = generateSubPeriods(l2.sign, l2.fromTime, l2Duration, 3)
          allPeriods.addAll(l3Periods)

          if (maxLevel >= 4) {
            for (l3 in l3Periods) {
              val l3Duration = l3.toTime.value - l3.fromTime.value
              val l4Periods = generateSubPeriods(l3.sign, l3.fromTime, l3Duration, 4)
              allPeriods.addAll(l4Periods)
            }
          }
        }
      }
    }
  }

  return allPeriods.sortedWith(compareBy({ it.fromTime }, { it.level }))
}
