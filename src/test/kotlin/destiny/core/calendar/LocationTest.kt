/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 5:20:43
 */
package destiny.core.calendar

import destiny.core.News.EastWest
import destiny.core.News.NorthSouth
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class LocationTest {

  private val logger = KotlinLogging.logger { }

//  @Test
//  fun testJson() {
//    val loc = locationOf(Locale.TAIWAN)
//    Json.stringify(Location.serializer(), loc).also {
//      assertTrue(it.isNotEmpty())
//      logger.info("json = {}", it)
//      assertTrue(it.contains(""""tzid":"Asia/Taipei""""))
//      Json.parse(Location.serializer(), it).also { parsed ->
//        logger.info("parsed = {}", parsed)
//        assertEquals(loc, parsed)
//      }
//    }
//  }


  @Test
  fun `沒帶入 tzid , 但有帶入 minuteOffset , 將會反查 tzid 找出相符合的 tzid`() {
    val loc = Location(25.0, 121.0, null, 480, null)
    /** 定義於 [java.time.ZoneId.SHORT_IDS] */
    assertEquals("Etc/GMT-8", loc.zoneId.id)
    assertEquals(480, loc.finalMinuteOffset)
  }

  @Test
  fun `有帶入 tzid , 但帶入非平時的 minuteOffset`() {
    val loc = Location(25.0, 121.0, "Asia/Taipei", 540, null)
    assertEquals("Asia/Taipei", loc.zoneId.id)
    assertEquals(540, loc.finalMinuteOffset)
  }

  @Test
  fun testNorthSouth() {
    assertSame(NorthSouth.NORTH, NorthSouth.of('N'))
    assertSame(NorthSouth.NORTH, NorthSouth.of('n'))
    assertSame(NorthSouth.SOUTH, NorthSouth.of('S'))
    assertSame(NorthSouth.SOUTH, NorthSouth.of('s'))
  }

  @Test
  fun testEastWest() {
    assertSame(EastWest.EAST, EastWest.of('E'))
    assertSame(EastWest.EAST, EastWest.of('e'))
    assertSame(EastWest.WEST, EastWest.of('W'))
    assertSame(EastWest.WEST, EastWest.of('w'))
  }

  @Test
  fun testLocation() {
    assertEquals(
      Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!),
      Location(-12, 23, 45.0, -23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!))

    assertEquals(
      Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!),
      Location(12, 23, 45.0, -23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!))

    assertEquals(
      Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!),
      Location(-12, 23, 45.0, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!))

    assertEquals(
      Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!),
      Location(12, 23, 45.0, 23, 34, 56.0, TimeTools.findZoneIdByMinutes(120)!!))
  }


  @Test
  fun testLocationEastWestDoubleNorthSouthDoubleInt() {
    val location = Location(EastWest.EAST, 121.51, NorthSouth.NORTH, 25.33, "Asia/Taipei")
    assertEquals(121, location.lngDeg.toLong())
    assertEquals(30, location.lngMin.toLong())
    assertEquals(36.0, location.lngSec)

    assertEquals(25, location.latDeg.toLong())
    assertEquals(19, location.latMin.toLong())
    assertEquals(48.0, location.latSec)
  }

  @Test
  fun testLocationOfTzid() {
    assertEquals(locationOf(Locale.TAIWAN), locationOf("Asia/Taipei"))
  }

}
