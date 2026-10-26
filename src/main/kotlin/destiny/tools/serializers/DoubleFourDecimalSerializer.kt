/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.tools.serializers

/** 四位小數：停滯前後的速度、外行星的黃緯都小於 0.01，兩位小數會捨成 0 或畫成階梯 */
object DoubleFourDecimalSerializer : DoubleTruncateSerializer(4)
