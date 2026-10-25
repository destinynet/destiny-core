/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core

import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.display
import kotlinx.serialization.Serializable

/**
 * 命盤的**身分區**資料 —— 與盤面 DTO（`ZiweiPlateDto`、`HoroscopeChartDto`…）是**兄弟關係**。
 *
 * 盤面 DTO 結構上沒有身分欄位（去識別化由編譯器把關）；要顯示身分時，呼叫端另外組這一份並列送出。
 * 不給就不畫，盲測與公開頁面因此不必另寫一套 renderer。
 *
 * 字串在這裡格式化一次（[of]），各呼叫端不各寫一份日期格式。
 *
 * @property displayName 顯示用姓名
 * @property gender `M`／`F`
 * @property birthText 國曆出生時間，`yyyy-MM-dd HH:mm`；只知道日期時為 `yyyy-MM-dd`
 * @property lunarText 農曆生日，如 `甲子年閏八月十五`（[ChineseDate.display]）
 * @property place 出生地
 */
@Serializable
data class ChartIdentityDto(
  val displayName: String? = null,
  val gender: String? = null,
  val birthText: String? = null,
  val lunarText: String? = null,
  val place: String? = null,
) {
  companion object {

    /**
     * @param year 與 [month]、[day] 任一為 null ⇒ 不印國曆
     * @param hour 與 [minute] 同時給才印時刻；只知道日期（或只知道時辰）時都傳 null
     */
    fun of(
      name: String?,
      gender: Gender?,
      year: Int?, month: Int?, day: Int?,
      hour: Int?, minute: Int?,
      chineseDate: ChineseDate?,
      place: String?,
    ): ChartIdentityDto {
      val date = if (year != null && month != null && day != null) "${year.pad(4)}-${month.pad(2)}-${day.pad(2)}" else null
      val birthText = if (date != null && hour != null && minute != null) "$date ${hour.pad(2)}:${minute.pad(2)}" else date
      return ChartIdentityDto(
        displayName = name?.takeIf { it.isNotBlank() },
        gender = gender?.code,
        birthText = birthText,
        lunarText = chineseDate?.display(),
        place = place?.takeIf { it.isNotBlank() },
      )
    }

    private fun Int.pad(width: Int): String = toString().padStart(width, '0')
  }
}
