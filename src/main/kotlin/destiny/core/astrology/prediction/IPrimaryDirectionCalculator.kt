/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.IHoroscopeModel
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Serializable


/** 時間鑰匙的介面，定義了弧角如何轉換為時間 */
interface ITimeKey {
  fun getArc(years: Double): Double
}

/** 托勒密之鑰：1度赤經 = 1年生命 */
@Serializable
object PtolemyKey : ITimeKey {
  override fun getArc(years: Double): Double = years
}

/** Naibod之鑰：1年 = 0.9856度 (太陽每日平均移動速度) */
@Serializable
object NaibodKey : ITimeKey {
  private const val NAIBOD_ARC_PER_YEAR = 360.0 /  TROPICAL_YEAR_DAYS // 0.98564736
  override fun getArc(years: Double): Double = years * NAIBOD_ARC_PER_YEAR
}

/** 代表一個主限法事件 */
data class DirectionEvent(
  val significator: AstroPoint,
  val promissor: AstroPoint,
  val arc: Double, // 推進的弧角
  val years: Double, // 對應的年數
  val eventGmt: GmtJulDay // 事件發生的約略時間
)

interface IPrimaryDirectionCalculator {
  fun getDirectionEvents(
    natalChart: IHoroscopeModel,
    significators: Set<AstroPoint>,
    promissors: Set<AstroPoint>,
    timeKey: ITimeKey,
    houseSystem: HouseSystem
  ): List<DirectionEvent>
}
