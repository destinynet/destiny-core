/**
 * Created by smallufo on 2026-07-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Ew3dDtoTest {

  private val logger = KotlinLogging.logger { }

  private val dto = Ew3dDto(
    eightWords = Ew3dDto.Pillars("壬寅", "丙午", "癸卯", "壬子"),
    time = LocalDateTime.of(2022, 6, 23, 23, 30),
    gmtOffsetMinutes = 480,
    location = locationOf(Locale.TAIWAN),
    place = "台北市",
    chineseDate = "壬寅年五月廿四",
    sunLongitude = 90.53,
    solarTerms = listOf(
      Ew3dDto.SolarTermEventDto("夏至", 90, LocalDateTime.of(2022, 6, 21, 17, 13, 40)),
      Ew3dDto.SolarTermEventDto("小暑", 105, LocalDateTime.of(2022, 7, 7, 10, 37, 49))
    ),
    hourBranches = listOf(
      Ew3dDto.HourBranchDto("子", LocalDateTime.of(2022, 6, 23, 23, 6, 5)),
      Ew3dDto.HourBranchDto("丑", LocalDateTime.of(2022, 6, 24, 1, 6, 12))
    ),
    meridianTime = LocalDateTime.of(2022, 6, 23, 11, 59, 30),
    nadirTime = LocalDateTime.of(2022, 6, 23, 23, 59, 45)
  )

  @Test
  fun testSerialize() {
    val json = Json.encodeToString(dto)
    logger.info { json }

    assertTrue(json.contains(""""year":"壬寅""""))
    assertTrue(json.contains(""""sunLongitude":90.53"""))
    assertTrue(json.contains(""""lat""""))
    assertTrue(json.contains(""""lng""""))
    // ISO datetime strings
    assertTrue(json.contains("2022-06-23T23:30"))
    assertTrue(json.contains("2022-06-21T17:13:40"))
  }

  @Test
  fun testRoundTrip() {
    val json = Json.encodeToString(dto)
    assertEquals(dto, Json.decodeFromString<Ew3dDto>(json))
  }
}
