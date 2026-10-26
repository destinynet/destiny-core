/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.astrology

import destiny.core.astrology.RetrogradePhase.*
import destiny.core.astrology.StationaryType.DIRECT_TO_RETROGRADE
import destiny.core.astrology.StationaryType.RETROGRADE_TO_DIRECT
import destiny.core.calendar.GmtJulDay
import kotlin.test.Test
import kotlin.test.assertEquals

class RetrogradeCycleTest {

  /** 2026 年第三次水逆（UTC）：陰影期 10-04 → 停滯轉逆 10-24 天蠍 20.98° → 停滯轉順 11-13 天蠍 5.03° → 離開陰影 11-30 */
  private val cycle = RetrogradeCycle(
    star = Planet.MERCURY,
    preparingGmt = GmtJulDay(2461317.881), preparingPos = Pos(215.03, 0.0),
    retrogradingGmt = GmtJulDay(2461337.800), retrogradingPos = Pos(230.98, 0.0),
    returningGmt = GmtJulDay(2461358.162), returningPos = Pos(215.03, 0.0),
    leavingGmt = GmtJulDay(2461374.762), leavingPos = Pos(230.98, 0.0)
  )

  /** 兩次停滯各自帶「該次停滯」的時刻與位置 —— 逆轉順那次曾誤帶順轉逆的位置 */
  @Test
  fun stationaries_carryOwnPositions() {
    val (toRetro, toDirect) = cycle.stationaries

    assertEquals(DIRECT_TO_RETROGRADE, toRetro.type)
    assertEquals(cycle.retrogradingGmt, toRetro.gmtJulDay)
    assertEquals(cycle.retrogradingPos, toRetro.pos)

    assertEquals(RETROGRADE_TO_DIRECT, toDirect.type)
    assertEquals(cycle.returningGmt, toDirect.gmtJulDay)
    assertEquals(cycle.returningPos, toDirect.pos)
  }

  @Test
  fun phaseMap_spansAreContiguous() {
    val map = cycle.phaseMap

    assertEquals(cycle.preparingGmt, map.getValue(PREPARING).begin)
    assertEquals(map.getValue(PREPARING).end, map.getValue(RETROGRADING).begin)
    assertEquals(map.getValue(RETROGRADING).end, map.getValue(LEAVING).begin)
    assertEquals(cycle.leavingGmt, map.getValue(LEAVING).end)

    assertEquals(cycle.returningPos, map.getValue(RETROGRADING).toPos)
    assertEquals(cycle.returningPos, map.getValue(LEAVING).fromPos)
  }
}
