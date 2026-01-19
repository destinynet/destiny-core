/**
 * Created by smallufo on 2026-01-19.
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.ITerm
import destiny.core.astrology.prediction.TermDistributorPeriod
import destiny.core.astrology.prediction.TermDistributorTimeline
import destiny.core.calendar.GmtJulDay

/**
 * 取得指定時刻的界限主星 (Term Distributor)
 *
 * @param gmtJulDay 查詢時刻
 * @param termImpl 界限系統實作 (埃及或托勒密)
 * @param significator 起始點 (通常是 ASC 或 MC)
 * @param degreesPerYear 每年推進度數 (預設 1.0)
 */
fun IHoroscopeModel.getTermDistributor(
  gmtJulDay: GmtJulDay,
  termImpl: ITerm,
  significator: AstroPoint = Axis.RISING,
  degreesPerYear: Double = 1.0
): TermDistributorPeriod? {
  require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }

  val startDegree = getZodiacDegree(significator)
    ?: throw IllegalArgumentException("Significator $significator not found in horoscope")

  val yearsElapsed = (gmtJulDay.value - this.gmtJulDay.value) / 365.25
  val degreesProgressed = yearsElapsed * degreesPerYear

  // 計算當前推進到的度數
  val currentDegree = (startDegree.value + degreesProgressed).toZodiacDegree()

  // 取得當前所在的界限
  val termBound = termImpl.getTermBound(currentDegree)

  // 計算此界限的時間範圍
  val fromDegreeOffset = if (termBound.fromDegree.value >= startDegree.value) {
    termBound.fromDegree.value - startDegree.value
  } else {
    (360.0 - startDegree.value) + termBound.fromDegree.value
  }

  val toDegreeOffset = if (termBound.toDegree.value > startDegree.value) {
    termBound.toDegree.value - startDegree.value
  } else {
    (360.0 - startDegree.value) + termBound.toDegree.value
  }

  val fromYears = fromDegreeOffset / degreesPerYear
  val toYears = toDegreeOffset / degreesPerYear

  val fromTime = GmtJulDay(this.gmtJulDay.value + fromYears * 365.25)
  val toTime = GmtJulDay(this.gmtJulDay.value + toYears * 365.25)

  return TermDistributorPeriod(
    ruler = termBound.ruler,
    fromDegree = termBound.fromDegree,
    toDegree = termBound.toDegree,
    fromTime = fromTime,
    toTime = toTime
  )
}

/**
 * 取得完整的界限主星時間線 (Term Distributor / Distributor through the Bounds)
 *
 * @param termImpl 界限系統實作 (埃及或托勒密)
 * @param significator 起始點 (通常是 ASC 或 MC)
 * @param years 計算年數 (預設 120)
 * @param degreesPerYear 每年推進度數 (預設 1.0)
 */
fun IHoroscopeModel.getTermDistributorTimeline(
  termImpl: ITerm,
  significator: AstroPoint = Axis.RISING,
  years: Int = 120,
  degreesPerYear: Double = 1.0
): TermDistributorTimeline {
  val startDegree = getZodiacDegree(significator)
    ?: throw IllegalArgumentException("Significator $significator not found in horoscope")

  val totalDegrees = years * degreesPerYear
  val birthTime = this.gmtJulDay
  val periods = mutableListOf<TermDistributorPeriod>()

  var currentDegreeValue = startDegree.value
  var currentTime = birthTime
  var degreesTraversed = 0.0

  while (degreesTraversed < totalDegrees) {
    val termBound = termImpl.getTermBound(currentDegreeValue.toZodiacDegree())

    // 計算距離此界限終點還有多少度
    val degreesToEndOfTerm = if (termBound.toDegree.value > currentDegreeValue) {
      termBound.toDegree.value - currentDegreeValue
    } else {
      // 跨越 360/0 邊界
      (360.0 - currentDegreeValue) + termBound.toDegree.value
    }

    // 計算此時期實際要走的度數
    val remainingDegrees = totalDegrees - degreesTraversed
    val periodDegrees = minOf(degreesToEndOfTerm, remainingDegrees)

    val periodYears = periodDegrees / degreesPerYear
    val periodEndTime = GmtJulDay(currentTime.value + periodYears * 365.25)
    val periodEndDegree = (currentDegreeValue + periodDegrees).toZodiacDegree()

    periods.add(
      TermDistributorPeriod(
        ruler = termBound.ruler,
        fromDegree = currentDegreeValue.toZodiacDegree(),
        toDegree = periodEndDegree,
        fromTime = currentTime,
        toTime = periodEndTime
      )
    )

    degreesTraversed += periodDegrees
    currentDegreeValue = periodEndDegree.value
    currentTime = periodEndTime
  }

  return TermDistributorTimeline(significator, startDegree, periods)
}
