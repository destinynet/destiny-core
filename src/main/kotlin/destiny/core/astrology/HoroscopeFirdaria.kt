/**
 * Created by smallufo on 2026-01-19.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.Firdaria
import destiny.core.astrology.prediction.FirdariaMajorPeriod
import destiny.core.astrology.prediction.FirdariaSubPeriod
import destiny.core.astrology.prediction.FirdariaTimeline
import destiny.core.DayNight
import destiny.core.astrology.prediction.firdariaPeriodOverlapping
import destiny.core.astrology.prediction.FirdariaAbuMashar
import destiny.core.astrology.prediction.IFirdariaSequence
import destiny.core.astrology.prediction.majorRulerYearsMap
import destiny.core.calendar.GmtJulDay

/**
 * 晝夜生判定：[dayNight] 非 null 時直接採用（[BirthDataGrain.DAY_NIGHT_DIURNAL] 等
 * 「不知時刻但知晝夜」的情境 —— 此時盤面時刻是捏造的正午，太陽宮位不可信）；
 * null 時維持現行「太陽在地平線上（第 7~12 宮）」推導，需精確時刻。
 */
private fun IHoroscopeModel.isDiurnal(dayNight: DayNight?): Boolean {
  return dayNight?.let { it == DayNight.DAY } ?: run {
    val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    sunHouse in 7..12
  }
}

/** 法達一年的天數（儒略年）。主運長度以「年」計，換成日期時用它 */
private const val FIRDARIA_YEAR_DAYS = 365.25

/**
 * 從出生起、無限循環（每 75 年一輪）的主運序列，惰性產生。
 * 副運：七星主運各分七等份，從主運主星起、依 [sequence] 的七星順序輪替；交點主運沒有副運。
 */
private fun IHoroscopeModel.majorPeriods(diurnal: Boolean, sequence: IFirdariaSequence): Sequence<FirdariaMajorPeriod> {
  val fullMajorRulerSequence = sequence.majorRulers(diurnal)
  // 副運序列只包含七大行星，順序與主運序列一致
  val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

  return generateSequence(fullMajorRulerSequence) { it }.flatten()
    .runningFold(FirdariaMajorPeriod(Planet.SUN, gmtJulDay, gmtJulDay, emptyList())) { previous, ruler ->
      val startTime = previous.toTime
      val periodDays = majorRulerYearsMap.getValue(ruler) * FIRDARIA_YEAR_DAYS
      val endTime = startTime + periodDays

      val subPeriods = if (ruler is Planet) {
        val subPeriodDays = periodDays / 7.0
        val subStartIndex = planetarySequence.indexOf(ruler)
        (0 until 7).map { i ->
          val subStart = startTime + i * subPeriodDays
          // 最後一段的結束時間與主運精確吻合
          val subEnd = if (i == 6) endTime else subStart + subPeriodDays
          FirdariaSubPeriod(planetarySequence[(subStartIndex + i) % 7], subStart, subEnd)
        }
      } else emptyList()

      FirdariaMajorPeriod(ruler, startTime, endTime, subPeriods)
    }
    .drop(1) // 丟棄出生時刻的虛擬初始值
}

/**
 * @param sequence 主運排法（夜生盤交點的位置），預設 [FirdariaAbuMashar]
 */
fun IHoroscopeModel.getFirdariaPeriods(
  gmtJulDay: GmtJulDay, dayNight: DayNight? = null, sequence: IFirdariaSequence = FirdariaAbuMashar,
): Pair<FirdariaMajorPeriod, FirdariaSubPeriod?> {
  require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }

  val foundMajor = majorPeriods(isDiurnal(dayNight), sequence).first { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
  val foundSub = foundMajor.subPeriods.find { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
  return foundMajor to foundSub
}

fun IHoroscopeModel.getFirdaria(gmtJulDay: GmtJulDay, dayNight: DayNight? = null, sequence: IFirdariaSequence = FirdariaAbuMashar): Firdaria {
  val (majorPeriod, subPeriod) = getFirdariaPeriods(gmtJulDay, dayNight, sequence)
  val period = subPeriod ?: majorPeriod

  return Firdaria(majorPeriod.ruler, period.ruler, period.fromTime, period.toTime)
}

/**
 * 高效計算指定時間範圍內的法達星限時段。
 * 此方法使用惰性計算，避免預先產生整個時間線，可以處理任意長度的時間範圍。
 * @param from 開始時間
 * @param to 結束時間
 * @param sequence 主運排法（夜生盤交點的位置），預設 [FirdariaAbuMashar]
 * @return 一個包含所有與指定範圍重疊的 Firdaria 時段的列表。
 */
fun IHoroscopeModel.getRangeFirdaria(
  from: GmtJulDay, to: GmtJulDay, dayNight: DayNight? = null, sequence: IFirdariaSequence = FirdariaAbuMashar,
): List<Firdaria> =
  majorPeriods(isDiurnal(dayNight), sequence)
    .dropWhile { it.toTime < from } // 跳過所有在查詢範圍開始前就已結束的主運
    .takeWhile { it.fromTime < to } // 截取所有在查詢範圍結束前開始的主運
    .flatMap { firdariaPeriodOverlapping.invoke(it, from, to) }
    .toList()

/** @param sequence 主運排法（夜生盤交點的位置），預設 [FirdariaAbuMashar] */
fun IHoroscopeModel.getFirdariaTimeline(years: Int, dayNight: DayNight? = null, sequence: IFirdariaSequence = FirdariaAbuMashar): FirdariaTimeline {
  require(years > 0) { "Years must be positive." }

  val diurnal = isDiurnal(dayNight)
  val endGmt = gmtJulDay + years * FIRDARIA_YEAR_DAYS
  return FirdariaTimeline(diurnal, majorPeriods(diurnal, sequence).takeWhile { it.fromTime < endGmt }.toList())
}
