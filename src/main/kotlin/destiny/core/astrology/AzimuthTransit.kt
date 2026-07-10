/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.GmtJulDaySerializer
import java.io.Serializable
import kotlinx.serialization.Serializable as KSerializable

/**
 * 星體通過某地平方位角的一次事件
 */
@KSerializable
data class AzimuthTransit(
  /** 通過時刻 (GMT) */
  @KSerializable(with = GmtJulDaySerializer::class)
  val gmtJulDay: GmtJulDay,
  /** 該刻的真實地平方位角 (北=0, 東=90, 南=180, 西=270)。理論上等於查詢 target，附上供收斂誤差檢查 */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val azimuthDeg: Double,
  /** 該刻真實高度；< 0 表示在地平線下 */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val trueAltitude: Double,
  /** 該刻視高度 (考量大氣折射) */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val apparentAltitude: Double,
  /** 是否為「上升段」通過：高度正在增加 (東半天) = true；下降段 (西半天) = false。用來區分一日兩次通過 */
  val ascending: Boolean
) : Serializable
