/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.IHoroscopeModel
import destiny.core.calendar.GmtJulDay


/** 時間鑰匙的介面，定義了弧角如何轉換為時間 */
interface ITimeKey {
  fun convertArcToYears(arc: Double): Double
}

/** 托勒密之鑰：1度赤經 = 1年生命 */
object PtolemyKey : ITimeKey {
  override fun convertArcToYears(arc: Double): Double = arc
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
