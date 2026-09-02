/**
 * Created by smallufo on 2026-08-01.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.Situation
import destiny.core.Gender
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtractedEventsTest {

  private val json = Json

  private fun fixture(hourMinute: LocalTime? = null, dayNight: DayNight? = null) = ExtractedEvents(
    name = "某人", gender = Gender.M, birthDay = LocalDate.of(1984, 7, 21),
    hourMinute = hourMinute,
    lat = 25.0330, lng = 121.5654, tzid = "Asia/Taipei", place = "臺北市",
    intro = "測試",
    events = listOf(DayEvent(LocalDate.of(2020, 3, 3), Situation.OTHERS, "某事")),
    dayNight = dayNight
  )

  /**
   * [ExtractedEvents.birthGrain] 是 grain 推導的唯一定義點 —— 四級全覆蓋。
   * DAY_NIGHT 兩級曾因呼叫端自行以 `hourMinute != null` 二分而不可達。
   */
  @Test
  fun birthGrain_coversAllFourLevels() {
    assertEquals(BirthDataGrain.MINUTE, fixture(hourMinute = LocalTime.of(14, 32)).birthGrain())
    assertEquals(BirthDataGrain.DAY_NIGHT_DIURNAL, fixture(dayNight = DayNight.DAY).birthGrain())
    assertEquals(BirthDataGrain.DAY_NIGHT_NOCTURNAL, fixture(dayNight = DayNight.NIGHT).birthGrain())
    assertEquals(BirthDataGrain.DAY, fixture().birthGrain())
  }

  /** [ExtractedEvents.hourMinute] 非 null 時，[ExtractedEvents.dayNight] 被忽略（晝夜由盤面推導） */
  @Test
  fun birthGrain_hourMinuteWinsOverDayNight() {
    assertEquals(
      BirthDataGrain.MINUTE,
      fixture(hourMinute = LocalTime.of(14, 32), dayNight = DayNight.NIGHT).birthGrain()
    )
  }

  /** dayNight 欄位序列化 roundtrip；舊素材（無此欄位）反序列化不受影響 */
  @Test
  fun dayNight_serializationRoundTripAndBackwardCompat() {
    val nocturnal = fixture(dayNight = DayNight.NIGHT)
    val encoded = json.encodeToString(ExtractedEvents.serializer(), nocturnal)
    assertTrue("NIGHT" in encoded)
    assertEquals(nocturnal, json.decodeFromString(ExtractedEvents.serializer(), encoded))

    // 舊素材：沒有 dayNight 欄位 → null → DAY
    val legacy = json.decodeFromString(
      ExtractedEvents.serializer(),
      """
{
    "name": "某人", "gender": "M", "birthDay": "1984-07-21",
    "lat": 25.0330, "lng": 121.5654, "tzid": "Asia/Taipei", "place": "臺北市",
    "intro": "測試",
    "events": [ { "date": "2020-03-03", "situation": "OTHERS", "details": "某事" } ]
}
      """
    )
    assertEquals(BirthDataGrain.DAY, legacy.birthGrain())
  }
}
