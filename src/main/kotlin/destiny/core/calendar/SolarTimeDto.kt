/**
 * Created by smallufo on 2026-10-23.
 */
package destiny.core.calendar

import destiny.tools.JSerializable
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ILocationSerializer
import kotlinx.serialization.Serializable as KSerializable

/**
 * 真太陽時 3D 動畫（destiny-charts-web `<solar-time-chart>`）的資料合約，全部伺服器算好。
 *
 * 時間一律是 epoch 毫秒（UTC）；民用時間由 [civilOffsets] 分段換算，前端不碰時區資料庫。
 * 窗口 = 包含 focus 的太陽日：前一個子正 → 下一個子正（相鄰兩天首尾相接，任何時刻必落在某一窗）。
 * 設計：root docs/plans/2026-10-23-solar-time-3d-design.md §A
 */
@KSerializable
data class SolarTimeDto(
  @KSerializable(with = ILocationSerializer::class)
  val location: ILocation,
  val place: String? = null,

  /** 前一個子正（太陽下中天） */
  val dayFromMs: Long,
  /** 下一個子正 */
  val dayToMs: Long,
  /** 午正（太陽上中天） */
  val meridianMs: Long,
  /** 午正時太陽視高度；極夜為負 */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val meridianAltDeg: Double,
  /** 窗口內的日出／日落；極夜、永晝為 null */
  val sunriseMs: Long? = null,
  val sunsetMs: Long? = null,
  /** 出生（或輸入）時刻；[SolarTimeDtoFactory.forDate] 為 null */
  val focusMs: Long? = null,

  /** 時區的標準時差（秒，不含夏令時）；自訂時差也歸這裡；tzdb 的 LMT 年代即 LMT 偏移 */
  val standardOffsetSeconds: Int,
  /** 午正那一刻的均時差（秒）：真太陽時 = UTC + 經度 × 240 秒 + 均時差 */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val eotSecondsAtMeridian: Double,
  /** 窗口內的民用時差分段；第一段 fromMs = dayFromMs，DST 切換日會有兩段 */
  val civilOffsets: List<CivilOffset>,

  /** 13 筆：子正之前那個子時起點、丑…亥、窗口尾的子時起點（HourSolarTransImpl） */
  val hourBranches: List<HourStart>,

  /** 每 2 分鐘一筆（含窗口終點），tMin = 從 dayFrom 起算的分鐘 */
  val samples: List<Sample>,

  /** 午正所在民用年的每日均時差（分鐘，兩位小數，index 0 = 1/1，取每天 12:00 UTC） */
  val eotYear: List<Double>,
) : JSerializable {

  @KSerializable
  data class CivilOffset(val fromMs: Long, val offsetSeconds: Int, val dstSeconds: Int) : JSerializable

  /** [branch] = 地支中文（"子"…"亥"），同 Ew3dDto.HourBranchDto */
  @KSerializable
  data class HourStart(val branch: String, val startMs: Long) : JSerializable

  @KSerializable
  data class Sample(
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val tMin: Double,
    /** 北 = 0 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val azimuthDeg: Double,
    /** 視高度；< 0 = 地平線下 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val altitudeDeg: Double,
    /** 直射點緯度 = 太陽赤緯 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val subsolarLatDeg: Double,
    /** 直射點經度（東經為正，[−180, 180)） */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val subsolarLngDeg: Double,
  ) : JSerializable
}
