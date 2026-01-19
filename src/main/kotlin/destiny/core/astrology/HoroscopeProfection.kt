/**
 * Created by smallufo on 2026-01-19.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.Scale
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.Profection
import destiny.core.astrology.prediction.getDayProfection
import destiny.core.astrology.prediction.getMonthProfection
import destiny.core.calendar.GmtJulDay
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


/**
 * 根據指定的尺度，高效計算特定時刻的小限法狀態。
 * @param gmtJulDay 欲查詢的時刻。
 * @param scale 欲查詢的時間尺度（年、月等）。
 * @return 一個包含從 YEAR 到指定 scale 的所有小限法狀態的 Map。
 */
fun IHoroscopeModel.getProfection(gmtJulDay: GmtJulDay, scale: Scale): Map<Scale, Profection> {
  require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }
  require(this.getHouse(Axis.RISING) == 1) { "Annual Profection requires a chart with a valid Ascendant and house system." }

  val rulerImpl: IRuler = RulerPtolemyImpl
  val houseCuspSigns = (1..12).associateWith { houseNumber ->
    this.getCuspDegree(houseNumber).sign
  }
  val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
  val dayNight = if (sunHouse in 7..12) DayNight.DAY else DayNight.NIGHT

  val resultMap = mutableMapOf<Scale, Profection>()

  // --- 年度計算 ---
  val age = floor((gmtJulDay - this.gmtJulDay) / TROPICAL_YEAR_DAYS).toInt()
  val annualFromTime = this.gmtJulDay + (age * TROPICAL_YEAR_DAYS)
  val annualToTime = annualFromTime + TROPICAL_YEAR_DAYS

  val annualProfectedHouse = (age % 12) + 1
  val annualAscSign = houseCuspSigns.getValue(annualProfectedHouse)
  val annualLord = with(rulerImpl) {
    (annualAscSign.getRulerPoint(dayNight) ?: annualAscSign.getRulerPoint()) as Planet
  }
  resultMap[Scale.YEAR] = Profection(Scale.YEAR, annualLord, annualAscSign, annualProfectedHouse, annualFromTime, annualToTime)
  if (scale == Scale.YEAR) return resultMap

  // --- 月度計算 ---
  val daysIntoYear = gmtJulDay - annualFromTime
  val monthlyPeriodDuration = TROPICAL_YEAR_DAYS / 12.0
  val monthIndex = floor(daysIntoYear / monthlyPeriodDuration).toInt()
  resultMap[Scale.MONTH] = getMonthProfection(annualFromTime, annualProfectedHouse, monthIndex, houseCuspSigns, dayNight)
  if (scale == Scale.MONTH) return resultMap

  // --- 日度計算 ---
  val monthlyProfection = resultMap.getValue(Scale.MONTH)
  val daysIntoMonth = floor(gmtJulDay - monthlyProfection.fromTime).toInt()
  resultMap[Scale.DAY] = getDayProfection(monthlyProfection.fromTime, monthlyProfection.house, daysIntoMonth, houseCuspSigns, dayNight)
  if (scale == Scale.DAY) return resultMap

  // --- 其他尺度 (待辦) ---
  when (scale) {
    Scale.HOUR -> TODO("Hourly profection calculation is not yet implemented.")
    else       -> return resultMap // Should not happen if logic is correct
  }
}

/**
 * 根據指定的尺度，高效計算時間範圍內的小限法時段列表。
 * @param fromTime 開始時間
 * @param toTime 結束時間
 * @param scale 欲查詢的時間尺度（年、月等）。
 * @return 一個只包含指定 scale 的 Profect 時段的列表。
 */
fun IHoroscopeModel.getRangeProfections(fromTime: GmtJulDay, toTime: GmtJulDay, scale: Scale): List<Profection> {
  require(fromTime <= toTime) { "fromTime must be earlier than or equal to toTime." }
  require(this.getHouse(Axis.RISING) == 1) { "Annual Profection requires a chart with a valid Ascendant and house system." }

  val rulerImpl: IRuler = RulerPtolemyImpl
  val houseCuspSigns: Map<Int, ZodiacSign> = (1..12).associateWith { houseNumber ->
    this.getCuspDegree(houseNumber).sign
  }
  val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
  val dayNight = if (sunHouse in 7..12) DayNight.DAY else DayNight.NIGHT

  // 計算查詢範圍開始時的約略年齡，並往前推一年以確保能捕捉到重疊的區間
  val startAge = max(0, floor((fromTime - this.gmtJulDay) / TROPICAL_YEAR_DAYS).toInt() - 1)

  // 建立一個從 startAge 開始的無限、惰性年齡序列
  val annualPeriodSequence: Sequence<Pair<Triple<GmtJulDay, Int, Planet>, ZodiacSign>> = generateSequence(startAge) { it + 1 }
    .map { age ->
      val annualFromTime = this.gmtJulDay + (age * TROPICAL_YEAR_DAYS)
      val annualProfectedHouse = (age % 12) + 1
      val annualAscSign = houseCuspSigns.getValue(annualProfectedHouse)
      val annualLord = with(rulerImpl) {
        (annualAscSign.getRulerPoint(dayNight) ?: annualAscSign.getRulerPoint()) as Planet
      }
      // 暫存年度資訊，以便月度計算使用
      Triple(annualFromTime, annualProfectedHouse, annualLord) to annualAscSign
    }
    .takeWhile { (annualInfo, _) ->
      val (annualFromTime, _, _) = annualInfo
      annualFromTime < toTime // 當年度週期的開始時間超出查詢範圍時，停止生成
    }

  // 從年度序列展開，產生與查詢範圍重疊的月度序列
  val monthPeriodSequence: Sequence<Profection> = annualPeriodSequence
    .flatMap { (annualInfo, _) ->
      val (annualFromTime, annualProfectedHouse, _) = annualInfo
      (0 until 12).asSequence().map { monthIndex ->
        getMonthProfection(annualFromTime, annualProfectedHouse, monthIndex, houseCuspSigns, dayNight)
      }
    }
    .filter { it.fromTime < toTime && fromTime < it.toTime } // 區間重疊檢查

  return when (scale) {
    Scale.YEAR  -> {
      annualPeriodSequence
        .filter { (annualInfo, _) ->
          val (annualFromTime, _, _) = annualInfo
          val annualToTime = annualFromTime + TROPICAL_YEAR_DAYS
          // 區間重疊檢查
          annualFromTime < toTime && fromTime < annualToTime
        }
        .map { (annualInfo, ascSign) ->
          val (annualFromTime, house, lord) = annualInfo
          Profection(Scale.YEAR, lord, ascSign, house, annualFromTime, annualFromTime + TROPICAL_YEAR_DAYS)
        }.toList()
    }

    Scale.MONTH -> {
      monthPeriodSequence.toList()
    }

    Scale.DAY   -> {
      monthPeriodSequence
        .flatMap { monthlyProfection ->
          // 計算一個月大概有幾天，取整數，以產生足夠的日度小限
          val monthlyDurationDays = ceil(monthlyProfection.toTime - monthlyProfection.fromTime).toInt()
          // 產生該月份中每一天的序列
          (0 until monthlyDurationDays).asSequence().map { dayIndex ->
            getDayProfection(monthlyProfection.fromTime, monthlyProfection.house, dayIndex, houseCuspSigns, dayNight)
          }
        }
        .filter { it.fromTime < toTime && fromTime < it.toTime } // 再次過濾出與查詢範圍重疊的日期
        .toList()
    }

    Scale.HOUR  -> TODO("Hourly range profection calculation is not yet implemented.")
  }
}
