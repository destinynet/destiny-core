/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.tools.serializers

/** 六位小數：以地球半徑為單位的長度（1e-6 ≈ 6 公尺），全食帶只有一兩百公里寬，四位小數（約 0.6 公里）不夠細 */
object DoubleSixDecimalSerializer : DoubleTruncateSerializer(6)
