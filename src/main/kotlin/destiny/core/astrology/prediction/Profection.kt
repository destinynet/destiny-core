/**
 * Created by smallufo on 2025-08-17.
 */
package destiny.core.astrology.prediction

import destiny.core.DayNight
import destiny.core.Scale
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * 小限的一期。小限法以星座計，與宮制無關。
 *
 * @param ascSign 這一期的小限星座
 * @param house 小限星座是從上升星座起算的第幾個星座（1–12）；不是象限宮制的宮位
 * @param lord 小限星座的主星
 */
@Serializable
data class Profection(
  val scale: Scale,
  val lord: Planet,
  val ascSign: ZodiacSign,
  val house: Int,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
)

/**
 * 計算月度小限
 * @param houseCuspSigns 第 N 個位置 → 從上升星座起算的第 N 個星座
 */
fun getMonthProfection(annualFromTime: GmtJulDay, annualProfectedHouse: Int, monthIndex: Int, houseCuspSigns: Map<Int, ZodiacSign>, dayNight: DayNight): Profection {
  val monthlyPeriodDuration = TROPICAL_YEAR_DAYS / 12.0
  val monthlyFromTime = annualFromTime + (monthIndex * monthlyPeriodDuration)
  val monthlyToTime = monthlyFromTime + monthlyPeriodDuration
  val house = ((annualProfectedHouse - 1 + monthIndex) % 12) + 1
  val ascSign = houseCuspSigns.getValue(house)
  val rulerImpl: IRuler = RulerPtolemyImpl
  val lord = with(rulerImpl) {
    (ascSign.getRulerPoint(dayNight) ?: ascSign.getRulerPoint()) as Planet
  }
  return Profection(Scale.MONTH, lord, ascSign, house, monthlyFromTime, monthlyToTime)
}

/**
 * 計算日度小限
 * @param monthlyFromTime 當前月度小限的開始時間
 * @param monthlyProfectedHouse 當前月度小限的宮位
 * @param dayIndex 從月度小限開始算起的第幾天 (從 0 開始)
 * @param houseCuspSigns 第 N 個位置 → 從上升星座起算的第 N 個星座
 */
fun getDayProfection(monthlyFromTime: GmtJulDay, monthlyProfectedHouse: Int, dayIndex: Int, houseCuspSigns: Map<Int, ZodiacSign>, dayNight: DayNight): Profection {
  val dailyFromTime = monthlyFromTime + dayIndex
  val dailyToTime = dailyFromTime + 1.0 // 每日小限的區間為一天
  val house = ((monthlyProfectedHouse - 1 + dayIndex) % 12) + 1
  val ascSign = houseCuspSigns.getValue(house)
  val rulerImpl: IRuler = RulerPtolemyImpl
  val lord = with(rulerImpl) {
    (ascSign.getRulerPoint(dayNight) ?: ascSign.getRulerPoint()) as Planet
  }
  return Profection(Scale.DAY, lord, ascSign, house, dailyFromTime, dailyToTime)
}
