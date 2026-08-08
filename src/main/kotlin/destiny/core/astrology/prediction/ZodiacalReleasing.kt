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

import destiny.core.astrology.Arabic
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.classical.AbstractPtolemy
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.astrology.ArabicSerializer
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
  /**
   * Angularity of [sign] counted whole-sign from the Lot this timeline released from
   * (the Lot's own sign = 1st). The **same** reference sign is used at every level:
   * an L3 period is judged from the Lot, not from its L2 parent — see [zrAngularity].
   *
   * Stored rather than derived because the consumer is a serialized report: a derived
   * member would not appear in the JSON, leaving the reader to count whole-sign houses
   * modulo 12 by hand. [angularityFrom] remains available for hand-built periods.
   */
  val angularity: ZrAngularity,
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
    periods.add(ZodiacalReleasing(1, sign, rulerOf(sign), zrAngularity(lotSign, sign), currentTime, periodEnd))
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
 * @param lotSign the sign of the Lot the whole timeline released from — the reference for
 *   [ZodiacalReleasing.angularity] at **every** level. Required, deliberately without a default:
 *   defaulting it to [parentSign] would let any forgetful caller compute angularity from the
 *   wrong reference, and a wrong PEAK/CADENT looks entirely plausible in the output.
 * @param parentSign the zodiac sign of the parent period — where this level starts cycling,
 *   and the anchor for the Loosing-of-the-Bond jump. Equals [lotSign] only at level 2 of the
 *   Lot's own first L1 period.
 * @param parentFrom start time of the parent period
 * @param parentDuration total duration of the parent period in days
 * @param level the sub-period level (2, 3, 4, ...)
 */
fun generateSubPeriods(
  lotSign: ZodiacSign,
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
    periods.add(ZodiacalReleasing(level, sign, rulerOf(sign), zrAngularity(lotSign, sign), currentTime, endTime))
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
      periods.add(ZodiacalReleasing(level, sign, rulerOf(sign), zrAngularity(lotSign, sign), currentTime, endTime, isLoosingOfBond = true))
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
      val l2Periods = generateSubPeriods(lotSign, l1.sign, l1.fromTime, l1Duration, 2)
      allPeriods.addAll(l2Periods)

      if (maxLevel >= 3) {
        for (l2 in l2Periods) {
          val l2Duration = l2.toTime.value - l2.fromTime.value
          val l3Periods = generateSubPeriods(lotSign, l2.sign, l2.fromTime, l2Duration, 3)
          allPeriods.addAll(l3Periods)

          if (maxLevel >= 4) {
            for (l3 in l3Periods) {
              val l3Duration = l3.toTime.value - l3.fromTime.value
              val l4Periods = generateSubPeriods(lotSign, l3.sign, l3.fromTime, l3Duration, 4)
              allPeriods.addAll(l4Periods)
            }
          }
        }
      }
    }
  }

  return allPeriods.sortedWith(compareBy({ it.fromTime }, { it.level }))
}

/**
 * Whole-sign "house" distance (1..12) from [lotSign] to [periodSign],
 * counting [lotSign] itself as 1.
 */
fun signHouseFrom(lotSign: ZodiacSign, periodSign: ZodiacSign): Int =
  ((periodSign.index - lotSign.index + 12) % 12) + 1

/**
 * Angularity of a Zodiacal Releasing period's sign relative to the Lot it was released from.
 *
 * Hellenistic doctrine (Valens / Brennan): periods angular to the releasing Lot are
 * "advancing" — prominent / high-engagement; the 10th sign from the Lot marks the life PEAK
 * for that Lot's topic; cadent periods are declining.
 */
enum class ZrAngularity { PEAK, ANGULAR, SUCCEDENT, CADENT }

fun zrAngularity(lotSign: ZodiacSign, periodSign: ZodiacSign): ZrAngularity =
  when (signHouseFrom(lotSign, periodSign)) {
    10          -> ZrAngularity.PEAK
    1, 4, 7     -> ZrAngularity.ANGULAR
    2, 5, 8, 11 -> ZrAngularity.SUCCEDENT
    else        -> ZrAngularity.CADENT // 3, 6, 9, 12
  }

/**
 * Classify this period's angularity relative to the Lot's sign [lotSign] it released from.
 *
 * Generated periods already carry [ZodiacalReleasing.angularity]; this remains for periods
 * built by hand, or to re-read one against a *different* Lot.
 */
fun ZodiacalReleasing.angularityFrom(lotSign: ZodiacSign): ZrAngularity =
  zrAngularity(lotSign, this.sign)

/**
 * One Lot's Zodiacal Releasing state at a **single instant** — the L1..Lmax periods enclosing it,
 * one per level, outermost first.
 *
 * Grouped by Lot rather than flattened: a report normally carries both Fortune and Spirit, and a
 * flat list of periods cannot say which Lot a given period belongs to. Grouping also avoids
 * repeating [lot] / [lotSign] on every level.
 */
@Serializable
data class ZrSnapshot(
  @Serializable(with = ArabicSerializer::class)
  val lot: Arabic,
  /** Natal sign of [lot] — the reference for every period's [ZodiacalReleasing.angularity] */
  val lotSign: ZodiacSign,
  val periods: List<ZodiacalReleasing>
)

/**
 * Build a [ZrSnapshot]: the L1..[maxLevel] periods enclosing [gmt], one per level, outermost first.
 *
 * [lotSign] is passed in rather than read off a chart on purpose. The Lots are not part of the
 * default [destiny.core.astrology.HoroscopeConfig.points], so `chart.getZodiacSign(lot)` returns
 * null for an ordinary chart — an overload taking the chart would quietly yield nothing at all.
 * The caller resolves the Lot's position (which needs the Ascendant, hence a known birth time)
 * and states it here.
 *
 * Two subtleties this does not share with [destiny.core.astrology.getRangeZodiacalReleasing]:
 * containment is half-open `[fromTime, toTime)`, so a query landing exactly on a changeover still
 * reports the period that is starting; and generation runs **past** [gmt], because
 * [generateZodiacalReleasing] truncates its final period at `endTime` — stopping at [gmt] would
 * report the enclosing L1 as ending today rather than years from now.
 *
 * @param natalGmt birth time — where every level's cycle starts
 * @param maxLevel depth; L4 periods run ~5 hours and mean nothing at day precision
 */
fun zrSnapshotAt(
  lot: Arabic,
  lotSign: ZodiacSign,
  natalGmt: GmtJulDay,
  gmt: GmtJulDay,
  maxLevel: Int = 3
): ZrSnapshot {
  // the longest possible L1 (Aquarius, 30 Egyptian years) — enough for the period holding gmt to complete
  val margin = zodiacalReleasingYears.values.max() * EGYPTIAN_YEAR_DAYS
  val periods = generateZodiacalReleasing(lotSign, natalGmt, gmt + margin, maxLevel)
    .filter { it.fromTime <= gmt && gmt < it.toTime }
    .sortedBy { it.level }
  return ZrSnapshot(lot, lotSign, periods)
}
