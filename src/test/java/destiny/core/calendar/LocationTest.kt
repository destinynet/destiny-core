/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 5:20:43
 */
package destiny.core.calendar

import destiny.tools.location.TimeZoneUtils
import org.junit.Assert
import org.slf4j.LoggerFactory
import kotlin.test.Test

class LocationTest {

  private val logger = LoggerFactory.getLogger(javaClass)


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

  /**
   * 2012/03 之後的格式 , 尾方為 altitude TimeZone [minuteOffset]
   */
  @Test
  fun testLocationFromDebugString_format2012() {
    var location: Location
    var expected: Location

    location = Location.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", null , 12.3456)
    Assert.assertEquals(expected, location)

    //強制設定 minuteOffset = 0
    location = Location.fromDebugString(location.debugString + " 0")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", 0, 12.3456)
    Assert.assertEquals(expected, location)

    location = Location.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei 60")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", 60, 12.3456)
    Assert.assertEquals(expected, location)

    location = Location.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei -480")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", -480, 12.3456)
    Assert.assertEquals(expected, location)
  }

  @Test
  fun testLocationEastWestDoubleNorthSouthDoubleInt() {
    val location: Location
    location = Location(EastWest.EAST, 121.51, NorthSouth.NORTH, 25.33, "Asia/Taipei", null, 0.0)
    Assert.assertEquals(121, location.lngDeg.toLong())
    Assert.assertEquals(30, location.lngMin.toLong())
    Assert.assertEquals(36.0, location.lngSec, 0.0)

    Assert.assertEquals(25, location.latDeg.toLong())
    Assert.assertEquals(19, location.latMin.toLong())
    Assert.assertEquals(48.0, location.latSec, 0.0)
  }


}
