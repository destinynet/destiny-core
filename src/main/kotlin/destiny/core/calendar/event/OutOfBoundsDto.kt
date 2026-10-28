/**
 * Created by smallufo on 2026-10-28.
 */
package destiny.core.calendar.event

import destiny.tools.serializers.DoubleFourDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 一次出界的赤緯曲線：窗口內等間隔的地心赤緯，以及出界、最深、回界三個時刻。全部伺服器算好。
 *
 * 時間一律 epoch 毫秒（UTC）；赤緯帶正負號（正＝北），取四位小數。
 * 交角只給一個值（最深那一刻）：窗口內交角的變化（含章動）不到 0.003°，畫不出差別。
 */
@Serializable
data class OutOfBoundsDto(
  /** nameKey，例：`Planet.PLUTO` */
  val planet: String,
  /** 顯示名（依請求語系） */
  val planetName: String,
  val planetGlyph: String,
  /** 往北出界 */
  val north: Boolean,

  val enteringMs: Long,
  val peakMs: Long,
  val leavingMs: Long,

  @Serializable(with = DoubleFourDecimalSerializer::class) val peakDeclination: Double,
  @Serializable(with = DoubleFourDecimalSerializer::class) val obliquity: Double,

  /** 窗口：出界前、回界後各留一段界內，看得出「越過邊界 → 折返 → 回來」 */
  val windowFromMs: Long,
  val windowToMs: Long,

  /** 窗口內等間隔取樣（含兩端），依時間遞增 */
  val samples: List<Sample>,
) {

  @Serializable
  data class Sample(
    val tMs: Long,
    /** 地心赤緯 */
    @Serializable(with = DoubleFourDecimalSerializer::class) val declination: Double,
  )
}
