/**
 * Created by smallufo on 2026-08-30.
 */
package destiny.core.astrology

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * ⚠️ **既有已序列化的資料沒有後來才加的欄位。**
 *
 * 每次往 [AstroEvent] 的子型別補欄位（2026-08-28 的 `Eclipse.zodiacDegree`、
 * 2026-08-30 的三個 `transitToNatalAspects`），只要漏了預設值，
 * 舊資料就會在反序列化時整批炸掉 —— 而那是在讀取時才發現的執行期錯誤，不是編譯錯誤。
 *
 * `Json` 預設 `encodeDefaults = false`，所以**以預設值建構再編碼，產出的就是「舊格式」**；
 * 用它 round-trip 即可證明相容，不必手寫一份會隨型別演化而過期的 JSON 字面值。
 */
class AstroEventSerializationTest {

  private val json = Json

  private fun assertOldFormatRoundTrips(event: AstroEvent, absentField: String) {
    val encoded = json.encodeToString(event)
    assertFalse(
      encoded.contains(absentField),
      "以預設值建構時該欄位不應被寫出（encodeDefaults=false），否則這份 JSON 就不是舊格式：$encoded"
    )
    assertEquals(event, json.decodeFromString<AstroEvent>(encoded), "舊格式必須能反序列化回來")
  }

  @Test
  fun `SignIngress 的舊格式沒有 transitToNatalAspects，仍須可反序列化`() {
    assertOldFormatRoundTrips(
      AstroEvent.SignIngress("Mars enters Aquarius", Planet.MARS, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS),
      "transitToNatalAspects"
    )
  }

  @Test
  fun `HouseIngress 的舊格式沒有 transitToNatalAspects，仍須可反序列化`() {
    assertOldFormatRoundTrips(
      AstroEvent.HouseIngress("Mars enters House 5", Planet.MARS, 4, 5),
      "transitToNatalAspects"
    )
  }

  @Test
  fun `OobIngress 的舊格式沒有 natalParallels 與 transitToNatalAspects，仍須可反序列化`() {
    assertOldFormatRoundTrips(
      AstroEvent.OobIngress("Venus enters OOB", Planet.VENUS, true, 23.9),
      "transitToNatalAspects"
    )
  }
}
