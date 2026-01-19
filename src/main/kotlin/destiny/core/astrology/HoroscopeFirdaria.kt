/**
 * Created by smallufo on 2026-01-19.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.Firdaria
import destiny.core.astrology.prediction.FirdariaMajorPeriod
import destiny.core.astrology.prediction.FirdariaSubPeriod
import destiny.core.astrology.prediction.FirdariaTimeline
import destiny.core.astrology.prediction.firdariaPeriodOverlapping
import destiny.core.astrology.prediction.getMajorRulers
import destiny.core.astrology.prediction.majorRulerYearsMap
import destiny.core.calendar.GmtJulDay


fun IHoroscopeModel.getFirdariaPeriods(gmtJulDay: GmtJulDay): Pair<FirdariaMajorPeriod, FirdariaSubPeriod?> {
  require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }

  val sunHouse = this.getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
  val diurnal = sunHouse in 7..12

  val fullMajorRulerSequence = getMajorRulers(diurnal)
  // 副運序列只包含七大行星，順序與主運序列一致
  val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

  // 2. 建立一個無限循環的主星序列，以處理 > 75 歲的情況
  val infiniteRulerSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
  val yearDays = 365.25

  // 3. 使用 scan 產生一個主運時期的無限序列
  // scan 的累加器 (accumulator) 是一個 Pair，儲存 (剛產生出來的主運, 下一個主運的開始時間)
  val initialAcc: Pair<FirdariaMajorPeriod?, GmtJulDay> = null to this.gmtJulDay

  val majorPeriodSequence = infiniteRulerSequence
    .scan(initialAcc) { acc, majorRuler ->
      val startTime = acc.second
      val periodYears = majorRulerYearsMap.getValue(majorRuler)
      val periodDays = periodYears * yearDays
      val endTime = startTime + periodDays

      val subPeriods = if (majorRuler is Planet) {
        val subPeriodDays = periodDays / 7.0
        val subStartIndex = planetarySequence.indexOf(majorRuler)
        (0 until 7).map { i ->
          val subStart = startTime + subPeriodDays * i
          val subEnd = if (i == 6) endTime // 保證最後一段對齊
          else subStart + subPeriodDays
          FirdariaSubPeriod(
            ruler = planetarySequence[(subStartIndex + i) % 7],
            fromTime = subStart,
            toTime = subEnd
          )
        }
      } else emptyList()

      FirdariaMajorPeriod(majorRuler, startTime, endTime, subPeriods) to endTime
    }
    .drop(1)
    .map { it.first!! }

  val foundMajor = majorPeriodSequence.first { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
  val foundSub = foundMajor.subPeriods.find { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }

  return foundMajor to foundSub
}

fun IHoroscopeModel.getFirdaria(gmtJulDay: GmtJulDay): Firdaria {
  val (majorPeriod, subPeriod) = getFirdariaPeriods(gmtJulDay)
  val period = subPeriod ?: majorPeriod

  return Firdaria(majorPeriod.ruler, period.ruler, period.fromTime, period.toTime)
}

/**
 * 高效計算指定時間範圍內的法達星限時段。
 * 此方法使用惰性計算，避免預先產生整個時間線，可以處理任意長度的時間範圍。
 * @param from 開始時間
 * @param to 結束時間
 * @return 一個包含所有與指定範圍重疊的 Firdaria 時段的列表。
 */
fun IHoroscopeModel.getRangeFirdaria(from: GmtJulDay, to: GmtJulDay): List<Firdaria> {
  val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
  val diurnal = sunHouse in 7..12

  val fullMajorRulerSequence = getMajorRulers(diurnal)
  val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()
  val yearDays = 365.25

  // 建立一個無限、惰性的主運序列
  val majorPeriodSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
    .runningFold(
      // 初始累加器：一個在出生時間點結束的虛擬週期
      FirdariaMajorPeriod(Planet.SUN, this.gmtJulDay, this.gmtJulDay, emptyList())
    ) { previousPeriod, currentRuler ->
      val startTime = previousPeriod.toTime
      val periodYears = majorRulerYearsMap.getValue(currentRuler)
      val periodDays = periodYears * yearDays
      val endTime = startTime + periodDays

      val subPeriods = if (currentRuler is Planet) {
        val subPeriodDays = periodDays / 7.0
        val subStartIndex = planetarySequence.indexOf(currentRuler)
        (0 until 7).map { i ->
          val subRuler = planetarySequence[(subStartIndex + i) % 7]
          val subStart = startTime + i * subPeriodDays
          val subEnd = if (i == 6) endTime else subStart + subPeriodDays
          FirdariaSubPeriod(subRuler, subStart, subEnd)
        }
      } else emptyList()

      FirdariaMajorPeriod(currentRuler, startTime, endTime, subPeriods)
    }
    .drop(1) // 丟棄第一個虛擬的初始值

  // 從惰性序列中篩選出與 [from, to] 範圍重疊的時段
  return majorPeriodSequence
    .dropWhile { it.toTime < from } // 跳過所有在查詢範圍開始前就已結束的主運
    .takeWhile { it.fromTime < to } // 截取所有在查詢範圍結束前開始的主運
    .flatMap { firdariaPeriodOverlapping.invoke(it, from, to) }
    .toList()
}

fun IHoroscopeModel.getFirdariaTimeline(years: Int): FirdariaTimeline {
  require(years > 0) { "Years must be positive." }

  val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
  val diurnal = sunHouse in 7..12

  val fullMajorRulerSequence = getMajorRulers(diurnal)
  val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

  val infiniteRulerSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
  val yearDays = 365.25

  // 展開所有主運直到指定年數
  val startGmt = gmtJulDay
  val endGmt = startGmt + years * yearDays

  val majorPeriods = infiniteRulerSequence
    .runningFold(startGmt to null as FirdariaMajorPeriod?) { (currentStart, _), majorRuler ->
      val periodYears = majorRulerYearsMap.getValue(majorRuler)
      val periodDays = periodYears * yearDays
      val majorEnd = currentStart + periodDays

      val subPeriods = if (majorRuler is Planet) {
        val subPeriodDays = periodDays / 7.0
        val subStartIndex = planetarySequence.indexOf(majorRuler)
        (0 until 7).map { i ->
          val subRuler = planetarySequence[(subStartIndex + i) % 7]
          val subStart = currentStart + i * subPeriodDays
          // 確保最後一個副運的結束時間與主運的結束時間精確吻合
          val subEnd = if (i == 6) majorEnd else subStart + subPeriodDays
          FirdariaSubPeriod(subRuler, subStart, subEnd)
        }
      } else emptyList()

      majorEnd to FirdariaMajorPeriod(majorRuler, currentStart, majorEnd, subPeriods)
    }
    .mapNotNull { it.second }
    .takeWhile { it.fromTime < endGmt }
    .toList()

  return FirdariaTimeline(diurnal, majorPeriods)
}
