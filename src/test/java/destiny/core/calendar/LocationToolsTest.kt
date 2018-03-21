/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import kotlin.test.Test
import kotlin.test.assertEquals

class LocationToolsTest {

  @Test
  fun testDebugString() {
    LocationTools.run {
      assertEquals("50.0,100.0", getDebugString(Location(100.0, 50.0)))
      assertEquals("25.03,121.0 Asia/Taipei", getDebugString(Location(121.0, 25.03, "Asia/Taipei")))
      assertEquals("25.03,121.0 Asia/Taipei 480m", getDebugString(Location(121.0, 25.03, "Asia/Taipei", 480)))
      assertEquals("25.03,121.0 Asia/Taipei 480m 500.0", getDebugString(Location(121.0, 25.03, "Asia/Taipei", 480 , 500.0))) // 高度 500米

      assertEquals("40.0,73.0", getDebugString(Location(73.0, 40.0)))
      assertEquals("40.0,73.0 America/New_York", getDebugString(Location(73.0, 40.0 , "America/New_York")))
      assertEquals("40.0,73.0 America/New_York -300m", getDebugString(Location(73.0, 40.0 , "America/New_York" , -300)))
      assertEquals("40.0,73.0 America/New_York -300m 800.0", getDebugString(Location(73.0, 40.0 , "America/New_York" , -300 , 800.0))) // 高度 800米
    }
  }

  @Test
  fun testDebugStringNew() {
    LocationTools.run {
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0) , decodeDebugStringNew("25.03,121.0 Asia/Taipei 480m 500.0"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480) , decodeDebugStringNew("25.03,121.0 Asia/Taipei 480m"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei") , decodeDebugStringNew("25.03,121.0 Asia/Taipei"))
      assertEquals(Location(121.0,25.03) , decodeDebugStringNew("25.03,121.0"))
    }
  }


  /**
   * 2012/03 之後的格式 , 尾方為 altitude TimeZone [minuteOffset]
   */
  @Test
  fun testLocationFromDebugString_format2012() {
    var location: Location
    var expected: Location

    location = LocationTools.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", null , 12.3456)
    assertEquals(expected, location)

    //強制設定 minuteOffset = 0
    location = LocationTools.fromDebugString(location.debugString + " 0")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", 0, 12.3456)
    assertEquals(expected, location)

    location = LocationTools.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei 60")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", 60, 12.3456)
    assertEquals(expected, location)

    location = LocationTools.fromDebugString("+1213012.34+25 312.34 12.3456 Asia/Taipei -480")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", -480, 12.3456)
    assertEquals(expected, location)
  }

}