/**
 * Created by smallufo on 2026-10-27.
 */
package destiny.core.calendar.event

import destiny.core.calendar.SolarTimeDto
import destiny.tools.serializers.DoubleFourDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 一次逆行週期的動畫資料（destiny-charts-web `<retrograde-chart>`），全部伺服器算好。
 *
 * 兩個視角共用一條時間軸：
 * - 日心：太陽、地球、行星的位置與「地球看出去的視線」—— 說明為什麼會逆行
 * - 地心黃道帶：行星的地心黃經／黃緯 —— 說明在哪幾度前進、折返、再前進
 *
 * 時間一律 epoch 毫秒（UTC）；民用時間由 [civilOffsets] 分段換算，前端不碰時區資料庫（同 [SolarTimeDto]）。
 * 度數一律黃道座標、0–360；數值取四位小數（停滯前後的速度與外行星的黃緯都小於 0.01）。
 */
@Serializable
data class RetrogradeCycleDto(
  /** nameKey，例：`Planet.MERCURY` */
  val planet: String,
  /** 顯示名（依請求語系） */
  val planetName: String,
  val planetGlyph: String,
  /** 內行星（水、金）：逆行中段是下合（與太陽同度）；外行星：逆行中段是衝（與太陽相對） */
  val inner: Boolean,

  /** 進入陰影 */
  val preparingMs: Long,
  /** 停滯轉逆 */
  val retrogradingMs: Long,
  /** 停滯轉順 */
  val returningMs: Long,
  /** 離開陰影 */
  val leavingMs: Long,

  @Serializable(with = DoubleFourDecimalSerializer::class) val preparingLng: Double,
  @Serializable(with = DoubleFourDecimalSerializer::class) val retrogradingLng: Double,
  @Serializable(with = DoubleFourDecimalSerializer::class) val returningLng: Double,
  @Serializable(with = DoubleFourDecimalSerializer::class) val leavingLng: Double,

  /** 逆行中段與太陽成一直線的時刻：內行星＝下合、外行星＝衝；找不到為 null */
  val alignmentMs: Long? = null,

  /** 動畫窗口：週期前後各留一段順行，看得出「慢下來 → 折返 → 再加速」 */
  val windowFromMs: Long,
  val windowToMs: Long,

  /** 窗口內的民用時差分段；第一段 fromMs = windowFromMs */
  val civilOffsets: List<SolarTimeDto.CivilOffset>,

  /** 窗口內等間隔取樣（含兩端），依時間遞增 */
  val samples: List<Sample>,
) {

  @Serializable
  data class Sample(
    val tMs: Long,
    /** 地心黃經、黃緯 */
    @Serializable(with = DoubleFourDecimalSerializer::class) val geoLng: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val geoLat: Double,
    /** 地心黃經速度（度／日）；負＝逆行 */
    @Serializable(with = DoubleFourDecimalSerializer::class) val geoSpeed: Double,
    /** 日心黃經、黃緯、距離（AU） */
    @Serializable(with = DoubleFourDecimalSerializer::class) val helioLng: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val helioLat: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val helioDist: Double,
    /** 地球的日心黃經（＝太陽地心黃經 ＋ 180°）與距離（AU） */
    @Serializable(with = DoubleFourDecimalSerializer::class) val earthLng: Double,
    @Serializable(with = DoubleFourDecimalSerializer::class) val earthDist: Double,
  )
}
