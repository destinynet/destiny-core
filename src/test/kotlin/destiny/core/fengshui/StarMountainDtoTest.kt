/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.AzimuthTransit
import destiny.core.astrology.Planet
import destiny.core.astrology.TransPoint
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class StarMountainDtoTest {

  private val logger = KotlinLogging.logger { }

  private val dto = StarMountainDto(
    location = locationOf(Locale.TAIWAN),
    place = "台北市",
    gmtOffsetMinutes = 480,
    star = Planet.SUN,
    fromLmt = LocalDateTime.of(2024, 3, 20, 0, 0),
    toLmt = LocalDateTime.of(2024, 3, 21, 0, 0),
    mapImageDataUrl = "data:image/png;base64,iVBORw0KGgo=",
    mapZoom = 15,
    mapScale = 2,
    samples = listOf(
      StarMountainDto.Sample(0.0, 2.25, -64.75),
      StarMountainDto.Sample(2.0, 3.5, -64.5)
    ),
    transits = listOf(
      StarMountainDto.PlateTransits(
        StarMountainDto.Plate.EARTHLY,
        listOf(
          StarMountainDto.Entry(Mountain.卯, LocalDateTime.of(2024, 3, 20, 6, 1, 2), 361.25, 82.5),
          StarMountainDto.Entry(Mountain.甲, LocalDateTime.of(2024, 3, 20, 7, 2, 3), 422.5, 82.5, isRetreat = true)
        )
      )
    ),
    markers = listOf(
      StarMountainDto.EventMarker(TransPoint.MERIDIAN, LocalDateTime.of(2024, 3, 20, 12, 5, 6), 725.25)
    )
  )

  @Test
  fun testSerialize() {
    val rawJson = Json { encodeDefaults = true }.encodeToString(StarMountainDto.serializer(), dto)
    logger.info { rawJson }

    val docCtx = JsonPath.parse(rawJson)
    assertEquals("Planet.SUN", docCtx.read("$.star"))
    assertEquals(480, docCtx.read("$.gmtOffsetMinutes"))
    assertEquals("台北市", docCtx.read("$.place"))
    // ISO local datetime strings
    assertEquals("2024-03-20T00:00:00", docCtx.read("$.fromLmt"))
    assertEquals("2024-03-21T00:00:00", docCtx.read("$.toLmt"))
    // location 帶 lat/lng
    docCtx.read<Double>("$.location.lat")
    docCtx.read<Double>("$.location.lng")
    // map
    assertEquals("data:image/png;base64,iVBORw0KGgo=", docCtx.read("$.mapImageDataUrl"))
    assertEquals(15, docCtx.read("$.mapZoom"))
    assertEquals(2, docCtx.read("$.mapScale"))
    // samples 兩位小數
    assertEquals(0.0, docCtx.read("$.samples[0].tMin"))
    assertEquals(2.25, docCtx.read("$.samples[0].azimuthDeg"))
    assertEquals(-64.75, docCtx.read("$.samples[0].altitudeDeg"))
    // transits：盤別 / 山名 / 邊界方位角 / 折返旗標
    assertEquals("EARTHLY", docCtx.read("$.transits[0].plate"))
    assertEquals("卯", docCtx.read("$.transits[0].entries[0].mountain"))
    assertEquals("2024-03-20T06:01:02", docCtx.read("$.transits[0].entries[0].lmt"))
    assertEquals(82.5, docCtx.read("$.transits[0].entries[0].azimuthDeg"))
    assertEquals(false, docCtx.read("$.transits[0].entries[0].isRetreat"))
    assertEquals(true, docCtx.read("$.transits[0].entries[1].isRetreat"))
    // markers
    assertEquals("MERIDIAN", docCtx.read("$.markers[0].kind"))
    assertEquals("2024-03-20T12:05:06", docCtx.read("$.markers[0].lmt"))
  }

  @Test
  fun testRoundTrip() {
    val json = Json.encodeToString(StarMountainDto.serializer(), dto)
    assertEquals(dto, Json.decodeFromString(StarMountainDto.serializer(), json))
  }

  /** [MountainEntry] kotlinx 序列化：GmtJulDay 走 [destiny.tools.serializers.GmtJulDaySerializer]、azimuth 兩位小數 */
  @Test
  fun mountainEntryRoundTrip() {
    val entry = MountainEntry(Mountain.卯, GmtJulDay(2460389.5), 82.5, isRetreat = true)
    val rawJson = Json { encodeDefaults = true }.encodeToString(MountainEntry.serializer(), entry)
    logger.info { rawJson }

    val docCtx = JsonPath.parse(rawJson)
    assertEquals("卯", docCtx.read("$.mountain"))
    assertEquals(2460389.5, docCtx.read("$.gmtJulDay.julDay"))
    assertEquals(82.5, docCtx.read("$.azimuthDeg"))
    assertEquals(true, docCtx.read("$.isRetreat"))
    assertEquals(entry, Json.decodeFromString(MountainEntry.serializer(), rawJson))
  }

  /** [MountainTransit]（含 [AzimuthTransit]）kotlinx 序列化 round-trip；isRetreat 為 computed getter，不進 JSON */
  @Test
  fun mountainTransitRoundTrip() {
    val transit = MountainTransit(
      mountain = Mountain.午,
      enter = AzimuthTransit(GmtJulDay(2460389.75), 172.5, 64.9, 64.92, ascending = true),
      leave = AzimuthTransit(GmtJulDay(2460389.79), 187.5, 64.9, 64.92, ascending = false)
    )
    val rawJson = Json.encodeToString(MountainTransit.serializer(), transit)
    logger.info { rawJson }

    val docCtx = JsonPath.parse(rawJson)
    assertEquals("午", docCtx.read("$.mountain"))
    assertEquals(2460389.75, docCtx.read("$.enter.gmtJulDay.julDay"))
    assertEquals(172.5, docCtx.read("$.enter.azimuthDeg"))
    assertEquals(true, docCtx.read("$.enter.ascending"))
    assertEquals(187.5, docCtx.read("$.leave.azimuthDeg"))
    assertEquals(transit, Json.decodeFromString(MountainTransit.serializer(), rawJson))
  }
}
