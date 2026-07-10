/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import destiny.core.astrology.Star
import destiny.core.astrology.TransPoint
import destiny.core.calendar.ILocation
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import java.io.Serializable
import java.time.LocalDateTime
import kotlinx.serialization.Serializable as KSerializable

/**
 * 星曜過山 3D 盤（destiny-charts-web `<star-mountain-chart>`）的資料合約，全部伺服器算好。
 * 設計：charts-web docs/plans/2026-07-10-star-mountain-3d-design.md §A
 */
@KSerializable
data class StarMountainDto(
  @KSerializable(with = ILocationSerializer::class)
  val location: ILocation,
  val place: String? = null,
  /** 該地與 GMT 的分鐘位移，前端把 gmt → 顯示時間 */
  val gmtOffsetMinutes: Int,
  /** 單一星體（通常日/月），wire format 如 "Planet.SUN" */
  val star: Star,
  /** 區間起（當地 LMT，顯示用） */
  @KSerializable(with = LocalDateTimeSerializer::class)
  val fromLmt: LocalDateTime,
  @KSerializable(with = LocalDateTimeSerializer::class)
  val toLmt: LocalDateTime,

  /** 北向朝上的靜態地圖 PNG，base64 data-url。zoom 決定地板實際涵蓋範圍 */
  val mapImageDataUrl: String,
  val mapZoom: Int,
  /** 1/2；地圖像素 = size*scale */
  val mapScale: Int,

  /** 星體位置密集取樣，供平滑動畫與前端內插。tMin = 從區間起算的分鐘數 */
  val samples: List<Sample>,

  /** 三盤各一份「進入新山」事件；前端把啟用環的合併排序供步進 */
  val transits: List<PlateTransits>,

  /** 選配：升/落/中天/天底時刻（LMT 標記） */
  val markers: List<EventMarker> = emptyList(),

  /** 磁偏角（WMM，東偏為正；台北 ≈ −4.4° 西偏）。前端「磁北對齊」旋轉量與指北線方向 */
  @KSerializable(with = DoubleTwoDecimalSerializer::class)
  val magDeclinationDeg: Double = 0.0,

  /** 磁北盤「進入新山」事件（三盤各一份；盤面 = 原盤旋轉磁偏角，時刻與正北盤不同） */
  val magneticTransits: List<PlateTransits> = emptyList(),
) : Serializable {

  @KSerializable
  data class Sample(
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val tMin: Double,
    /** 北=0 慣例 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val azimuthDeg: Double,
    /** 視高度；<0 = 地平線下 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val altitudeDeg: Double,
  ) : Serializable

  @KSerializable
  data class PlateTransits(val plate: Plate, val entries: List<Entry>) : Serializable

  @KSerializable
  data class Entry(
    /** 進入的山 */
    val mountain: Mountain,
    /** 當地 LMT（顯示，伺服器 root-find 準到秒） */
    @KSerializable(with = LocalDateTimeSerializer::class)
    val lmt: LocalDateTime,
    /** 從區間起算分鐘（前端定位/步進） */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val tMin: Double,
    /** 邊界方位角 */
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val azimuthDeg: Double,
    /** 此筆是否為逆行折返造成（見 [MountainEntry.isRetreat]） */
    val isRetreat: Boolean = false,
  ) : Serializable

  @KSerializable
  data class EventMarker(
    val kind: TransPoint,
    @KSerializable(with = LocalDateTimeSerializer::class)
    val lmt: LocalDateTime,
    @KSerializable(with = DoubleTwoDecimalSerializer::class) val tMin: Double,
  ) : Serializable

  /** 地盤/人盤/天盤 */
  enum class Plate { EARTHLY, HUMAN, HEAVENLY }
}
