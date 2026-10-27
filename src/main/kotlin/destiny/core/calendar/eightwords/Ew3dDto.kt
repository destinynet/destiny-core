/**
 * Created by smallufo on 2026-07-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.tools.JSerializable
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/** ew3d（destiny-charts-web `<ew-chart>`）的資料合約。設計：charts-web docs/plans/2026-07-07-ew3d-design.md §A */
@Serializable
data class Ew3dDto(
  val eightWords: Pillars,
  @Serializable(with = LocalDateTimeSerializer::class)
  val time: LocalDateTime,
  val gmtOffsetMinutes: Int,
  @Serializable(with = ILocationSerializer::class)
  val location: ILocation,
  val place: String? = null,
  /** 顯示字串，後端組好（ChineseDate.display） */
  val chineseDate: String,
  /** 出生時刻太陽視黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val sunLongitude: Double,
  /** 出生前後約一年節氣時刻表（遞增） */
  val solarTerms: List<SolarTermEventDto>,
  /** 出生當日 12 時辰起點（依 [hourImpl] 切割，遞增） */
  val hourBranches: List<HourBranchDto>,
  @Serializable(with = LocalDateTimeSerializer::class)
  val meridianTime: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  val nadirTime: LocalDateTime,
  /**
   * 時辰的切法：[HourImpl.TST]＝真太陽時（時辰環對齊太陽時角）；[HourImpl.LMT]＝鐘錶時（23 點起每 2 小時），
   * viewer 依 [hourBranches] 的子初把時辰環轉到對應的時角。預設值不輸出，舊的 viewer 照真太陽時畫。
   */
  val hourImpl: HourImpl = HourImpl.TST,
) : JSerializable {

  @Serializable
  data class Pillars(val year: String, val month: String, val day: String, val hour: String)

  @Serializable
  data class SolarTermEventDto(
    val name: String,
    val sunLng: Int,
    @Serializable(with = LocalDateTimeSerializer::class) val time: LocalDateTime
  )

  @Serializable
  data class HourBranchDto(
    val branch: String,
    @Serializable(with = LocalDateTimeSerializer::class) val startTime: LocalDateTime
  )
}
