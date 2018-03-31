/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.tools.location.TimeZoneUtils
import org.junit.Assert
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationToolsTest {

  @Test
  fun testEncode2018() {
    LocationTools.run {
      assertEquals("50.0,100.0", encode2018(Location(100.0, 50.0)))
      assertEquals("50.456789,100.123456", encode2018(Location(100.123456, 50.456789)))
      assertEquals("25.03,121.0 Asia/Taipei", encode2018(Location(121.0, 25.03, "Asia/Taipei")))
      assertEquals("25.03,121.0 Asia/Taipei 480m", encode2018(Location(121.0, 25.03, "Asia/Taipei", 480)))
      assertEquals("25.03,121.0 Asia/Taipei 480m 500.0", encode2018(Location(121.0, 25.03, "Asia/Taipei", 480, 500.0))) // 高度 500米

      assertEquals("40.0,73.0", encode2018(Location(73.0, 40.0)))
      assertEquals("40.0,73.0 America/New_York", encode2018(Location(73.0, 40.0, "America/New_York")))
      assertEquals("40.0,73.0 America/New_York -300m", encode2018(Location(73.0, 40.0, "America/New_York", -300)))
      assertEquals("40.0,73.0 America/New_York -300m 800.0", encode2018(Location(73.0, 40.0, "America/New_York", -300, 800.0))) // 高度 800米
    }
  }

  @Test
  fun testDecode2018() {
    LocationTools.run {
      // 後方 時區、時差、高度 六種排列順序不影響結果
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 Asia/Taipei 480m 500.0"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 Asia/Taipei 500.0 480m"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 500.0 480m Asia/Taipei"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 500.0 Asia/Taipei 480m"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 480m 500.0 Asia/Taipei"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480 , 500.0), decode2018("25.03,121.0 480m Asia/Taipei 500.0"))

      // 南緯、西經
      assertEquals(Location(-121.0,-25.03 , "Asia/Taipei" , -480 , 500.0), decode2018("-25.03,-121.0 -480m Asia/Taipei 500.0"))

      assertEquals(Location(121.0,25.03 , "Asia/Taipei" , 480), decode2018("25.03,121.0 Asia/Taipei 480m"))
      assertEquals(Location(121.0,25.03 , "Asia/Taipei"), decode2018("25.03,121.0 Asia/Taipei"))
      assertEquals(Location(121.0,25.03), decode2018("25.03,121.0"))
    }
  }


  /**
   * 2012/03 之後的格式 , 尾方為 altitude TimeZone [minuteOffset]
   */
  @Test
  fun testLocationFromDebugString_format2012() {
    var location: ILocation
    var expected: ILocation

    location = LocationTools.decode2012("+1213012.44+25 312.44 12.3456 Asia/Taipei")
    expected = Location(EastWest.EAST, 121, 30, 12.44, NorthSouth.NORTH, 25, 3, 12.44, "Asia/Taipei", null , 12.3456)
    assertEquals(expected, location)

    //強制設定 minuteOffset = 0
    location = LocationTools.decode2012(LocationTools.encode2012(location) + " 0")
    expected = Location(EastWest.EAST, 121, 30, 12.44, NorthSouth.NORTH, 25, 3, 12.44, "Asia/Taipei", 0, 12.3456)
    assertEquals(expected, location)

    location = LocationTools.decode2012("+1213012.34+25 312.34 12.3456 Asia/Taipei 60")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", 60, 12.3456)
    assertEquals(expected, location)

    location = LocationTools.decode2012("+1213012.34+25 312.34 12.3456 Asia/Taipei -480")
    expected = Location(EastWest.EAST, 121, 30, 12.34, NorthSouth.NORTH, 25, 3, 12.34, "Asia/Taipei", -480, 12.3456)
    assertEquals(expected, location)
  }


  @Test
  fun testEncode2012() {
    var location: Location
    location = Location(EastWest.EAST, 120, 30, 12.50, NorthSouth.NORTH, 25, 3, 12.30, TimeZoneUtils.getTimeZone(480).id, null, 12.3456)
    Assert.assertEquals("+12030 12.5+25 312.30 12.3456 CTT", LocationTools.encode2012(location))

    location = Location(EastWest.EAST, 121, 30, 12.44, NorthSouth.NORTH, 25, 3, 12.44, TimeZoneUtils.getTimeZone(-60).id , null , 0.0)
    Assert.assertEquals("+1213012.44+25 312.44 0.0 Etc/GMT+1", LocationTools.encode2012(location))

    location = Location(EastWest.EAST, 121, 30, 12.44, NorthSouth.NORTH, 25, 3, 12.44, TimeZoneUtils.getTimeZone(-60).id, null, -1000.0)
    Assert.assertEquals("+1213012.44+25 312.44 -1000.0 Etc/GMT+1", LocationTools.encode2012(location))
  }

}