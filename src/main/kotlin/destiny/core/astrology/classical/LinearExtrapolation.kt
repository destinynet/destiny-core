/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.rays

/*
 * 以盤面時刻的位置與黃經速度線性外推「最近離開／下一個迎上」時共用的計算（不需要星曆）。
 * 技法本身（收哪些相位、取哪一筆、窗多寬）各在自己的實作裡，這裡只有兩邊都要的基本量
 */

/** [p] 的黃經速度（度／日）；盤上沒有速度時用平均日速（[AverageDailyMotionMap]） */
internal fun IHoroscopeModel.lngSpeedOrAverage(p: Planet): Double? =
  getStarPosition(p)?.speedLng ?: AverageDailyMotionMap.getAvgDailySpeed(p)

/** 「這顆星的黃經減去對方的黃經」（0–360）在此相位準確時應為哪些值：合相與對分一個，其餘前後各一個 */
internal val Aspect.exactSeparations: List<Double>
  get() = rays(0.0)

/**
 * 目前的黃經差 [separation]（0–360）離準確值 [exact] 還差幾度，帶號、落在 [-180, 180)：
 * 正 ＝ 已經超過準確值，負 ＝ 還沒到（黃經差隨相對速度增加時）
 */
internal fun signedOffset(separation: Double, exact: Double): Double = (separation - exact + 540).mod(360.0) - 180
