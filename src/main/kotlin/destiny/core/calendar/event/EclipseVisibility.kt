/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.core.calendar.event

/**
 * 某地看一次日食或月食，看得到多少。
 * 以「看得出來的那一段」的接觸點逐一判斷天體在不在地平線上，再歸納成一句話；哪一段算「看得出來」由呼叫端決定
 * （月食：有本影就是 U1–U4；日食：初虧到復圓）。
 */
enum class EclipseVisibility {
  /** 全程看得到 */
  ALL,
  /** 天體升起時已在食中（月出、日出） */
  RISE,
  /** 食到一半天體落下（月落、日落） */
  SET,
  /** 只看得到中段（高緯度才有） */
  SOME,
  /** 看不到 */
  NONE;

  companion object {
    /** @param ups 依時間排列：每個接觸點發生時，天體在不在地平線上 */
    fun of(ups: List<Boolean>): EclipseVisibility = when {
      ups.isEmpty() || ups.none { it } -> NONE
      ups.all { it }                   -> ALL
      !ups.first() && ups.last()       -> RISE
      ups.first() && !ups.last()       -> SET
      else                             -> SOME
    }
  }
}
