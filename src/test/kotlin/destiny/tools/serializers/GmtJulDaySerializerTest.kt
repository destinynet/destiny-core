/**
 * Created by smallufo on 2025-08-31.
 */
package destiny.tools.serializers

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GmtJulDaySerializerTest {
  private val json = Json {
    prettyPrint = true
  }

  /**
   * 測試 `GmtJulDay` 物件能否被正確序列化。
   * 驗證輸出的 JSON 字串是否包含正確的 `julDay` 值，
   * 以及 `gmt` 字串是否符合預期格式 (ISO 8601，截斷到分鐘)。
   */
  @Test
  fun testSerialization() {
    // 定義一個測試用的 Julian Day。
    // 這個值對應到 2025-08-31T16:20:30.000Z。
    val gmtJulDay = 2460919.180903.toGmtJulDay()

    val serializedString = json.encodeToString(gmtJulDay)

    // 將時間四捨五入並截斷到分鐘
    val expected = """
        {
            "julDay": 2460919.180903,
            "gmt": "2025-08-31T16:20"
        }
    """.trimIndent()

    assertEquals(expected, serializedString)
  }

  @Test
  fun testDeserialization_full() {
    val julianDayValue = 2460919.180903

    // 包含 `julDay` 和 `gmt` 欄位的 JSON 字串。
    // 故意給 `gmt` 欄位一個不正確的值來驗證它會被忽略。
    val jsonString = """
        {
            "julDay": $julianDayValue,
            "gmt": "2025-01-01T00:00"
        }
    """.trimIndent()

    // 反序列化 JSON 字串
    val deserializedGmtJulDay = json.decodeFromString<GmtJulDay>(jsonString)

    assertEquals(julianDayValue, deserializedGmtJulDay.value)
  }

  @Test
  fun testDeserialization_legacy() {
    // 原始的 Julian Day 值
    val julianDayValue = 2460919.180903

    val jsonString = julianDayValue.toString()

    // 反序列化 JSON 字串
    val deserializedGmtJulDay = json.decodeFromString<GmtJulDay>(jsonString)

    assertEquals(julianDayValue, deserializedGmtJulDay.value)
  }

  @Test
  fun testDeserialization_missingJulDay() {
    // 缺少 `julDay` 欄位的 JSON 字串
    val jsonString = """
        {
            "gmt": "2025-08-31T04:20"
        }
    """.trimIndent()

    // 預期拋出 SerializationException
    assertFailsWith<SerializationException> {
      json.decodeFromString<GmtJulDay>(jsonString)
    }
  }
}
