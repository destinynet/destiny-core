/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 5:20:43
 */
package destiny.core.calendar

import destiny.tools.location.TimeZoneUtils
import org.junit.Assert
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals

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
    Assert.assertSame(NorthSouth.NORTH, NorthSouth.getNorthSouth('N'))
    Assert.assertSame(NorthSouth.NORTH, NorthSouth.getNorthSouth('n'))
    Assert.assertSame(NorthSouth.SOUTH, NorthSouth.getNorthSouth('S'))
    Assert.assertSame(NorthSouth.SOUTH, NorthSouth.getNorthSouth('s'))
  }

  @Test
  fun testEastWest() {
    Assert.assertSame(EastWest.EAST, EastWest.getEastWest('E'))
    Assert.assertSame(EastWest.EAST, EastWest.getEastWest('e'))
    Assert.assertSame(EastWest.WEST, EastWest.getEastWest('W'))
    Assert.assertSame(EastWest.WEST, EastWest.getEastWest('w'))
  }

  @Test
  fun testLocation() {
    var actual: Location
    var expected: Location

    actual = Location(-12, 23, 45.0, -23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    println("actual = $actual")
    expected = Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id )
    println("expected = $expected")
    Assert.assertEquals(expected, actual)

    actual = Location(12, 23, 45.0, -23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.SOUTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    Assert.assertEquals(expected, actual)


    actual = Location(-12, 23, 45.0, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.WEST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    Assert.assertEquals(expected, actual)

    actual = Location(12, 23, 45.0, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    expected = Location(EastWest.EAST, 12, 23, 45.0, NorthSouth.NORTH, 23, 34, 56.0, TimeZoneUtils.getTimeZone(120).id)
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun testLocationDebugString() {
    var location: Location
    location = Location(EastWest.EAST, 121, 30, 12.30, NorthSouth.NORTH, 25, 3, 12.30, TimeZoneUtils.getTimeZone(480).id , null , 12.3456)

    Assert.assertEquals("+1213012.30+25 312.30 12.3456 CTT", location.debugString)

    location = Location(EastWest.EAST, 121, 30, 12.33, NorthSouth.NORTH, 25, 3, 12.33, TimeZoneUtils.getTimeZone(-60).id)
    Assert.assertEquals("+1213012.34+25 312.34 0.0 Etc/GMT+1", location.debugString)

    location = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, TimeZoneUtils.getTimeZone(-60).id, null , -1000.0)
    Assert.assertEquals("+1213012.34+25 312.34 -1000.0 Etc/GMT+1", location.debugString)

  }


  @Test
  fun testLocationEastWestDoubleNorthSouthDoubleInt() {
    val location = Location(EastWest.EAST, 121.51, NorthSouth.NORTH, 25.33, "Asia/Taipei")
    Assert.assertEquals(121, location.lngDeg.toLong())
    Assert.assertEquals(30, location.lngMin.toLong())
    Assert.assertEquals(36.0, location.lngSec, 0.0)

    Assert.assertEquals(25, location.latDeg.toLong())
    Assert.assertEquals(19, location.latMin.toLong())
    Assert.assertEquals(48.0, location.latSec, 0.0)
  }


}
