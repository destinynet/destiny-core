/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import destiny.core.astrology.AzimuthTransit
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.min

/**
 * 某星體「進入並離開」某座山的時間窗。
 *
 * @property enter 進入此山的邊界穿越（方位角遞增時為 startDeg、遞減時為 endDeg）
 * @property leave 離開此山的邊界穿越（正常穿越到對側時為另一邊界；逆行折返時與 [enter] 同一邊界）
 */
data class MountainTransit(
  val mountain: Mountain,
  val enter: AzimuthTransit,
  val leave: AzimuthTransit,
) : Serializable {

  /**
   * 是否「逆行折返」：由原邊界進入又由原邊界退出（未穿越到對側）。
   * 發生在星體赤緯 > 當地緯度、方位角在「大距」處反轉、且大距落在此山之內時。
   * 判定：[enter] 與 [leave] 的方位角落在同一邊界（相距遠小於一座山寬 15°）。
   */
  val isRetreat: Boolean
    get() = abs(enter.azimuthDeg - leave.azimuthDeg).let { min(it % 360.0, 360.0 - it % 360.0) } < 7.5
}
