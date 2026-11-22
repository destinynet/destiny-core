/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.astrology

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * 由天頂的黃經推出同一個地平座標下的上升黃經：天頂 → 天頂赤經（RAMC）→ 該緯度的上升。純球面三角，與宮制無關。
 *
 * 推運的軸點用它：天頂以某種方式推進之後（例如加上太陽弧），上升不另外推，而是由推進後的天頂在出生地緯度換算 ——
 * 兩個軸點因此仍是同一個地平座標的一對。拿本命天頂代入，會得回本命上升（測試以此對照）。
 *
 * @param mcLng 天頂黃經（度）
 * @param obliquity 黃赤交角（度）
 * @param latitude 地理緯度（度，北正南負）；極圈內上升沒有定義，結果不保證
 * @return 上升黃經（0 ≤ x < 360）
 */
fun ascendantFromMidheaven(mcLng: Double, obliquity: Double, latitude: Double): Double {
  val r = PI / 180
  val lam = mcLng * r
  val eps = obliquity * r
  val phi = latitude * r
  // 天頂在黃道上 ⇒ 由黃經換赤經（黃緯 0）
  val ramc = atan2(sin(lam) * cos(eps), cos(lam))
  val asc = atan2(cos(ramc), -(sin(ramc) * cos(eps) + tan(phi) * sin(eps)))
  return (asc / r).mod(360.0)
}
