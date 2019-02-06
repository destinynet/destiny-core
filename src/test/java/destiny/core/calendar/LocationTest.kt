/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 5:20:43
 */
package destiny.core.calendar

import destiny.tools.location.TimeZoneUtils
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LocationTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun `沒帶入 tzid , 但有帶入 minuteOffset , 將會反查 tzid 找出相符合的 tzid`() {
    val loc = Location(121.0 , 25.0 , null , 480 , null)
    assertEquals("CTT" , loc.timeZone.id)
    /** 定義於 [java.time.ZoneId.SHORT_IDS] */
    assertEquals("Asia/Shanghai" , loc.zoneId.id)
    println(loc.timeZone)
    assertEquals(480 , loc.finalMinuteOffset)
  }

  @Test
  fun `有帶入 tzid , 但帶入非平時的 minuteOffset`() {
    val loc = Location(121.0 , 25.0 , "Asia/Taipei" , 540 , null)
    assertEquals("Asia/Taipei" , loc.timeZone.id)
    assertEquals("Asia/Taipei" , loc.zoneId.id)
    println(loc.timeZone)
    assertEquals(540 , loc.finalMinuteOffset)
  }

  @Test
  fun testNorthSouth() {
    assertSame(NorthSouth.NORTH, NorthSouth.getNorthSouth('N'))
    assertSame(NorthSouth.NORTH, NorthSouth.getNorthSouth('n'))
    assertSame(NorthSouth.SOUTH, NorthSouth.getNorthSouth('S'))
    assertSame(NorthSouth.SOUTH, NorthSouth.getNorthSouth('s'))
  }

  @Test
  fun testEastWest() {
    assertSame(EastWest.EAST, EastWest.getEastWest('E'))
    assertSame(EastWest.EAST, EastWest.getEastWest('e'))
    assertSame(EastWest.WEST, EastWest.getEastWest('W'))
    assertSame(EastWest.WEST, EastWest.getEastWest('w'))
  }

  @Test
  fun testLocation() {
    var actual: Location
    var expected: Location

    actual = Location(-12, 23, 45.0, -23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    println("actual = $actual")
    expected = Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id )
    println("expected = $expected")
    assertEquals(expected, actual)

    actual = Location(12, 23, 45.0, -23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    assertEquals(expected, actual)


    actual = Location(-12, 23, 45.0, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    assertEquals(expected, actual)

    actual = Location(12, 23, 45.0, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    assertEquals(expected, actual)
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


}
