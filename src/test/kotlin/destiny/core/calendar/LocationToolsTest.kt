/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.core.News.EastWest.EAST
import destiny.core.News.NorthSouth.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationToolsTest {

  @Test
  fun testEncode2018() {
    LocationTools.run {
      assertEquals("50.0,100.0", encode2018(Location(50.0, 100.0)))
      assertEquals("50.456789,100.123456", encode2018(Location(50.456789, 100.123456)))
      assertEquals("25.03,121.0 Asia/Taipei", encode2018(Location(25.03, 121.0, "Asia/Taipei")))
      assertEquals("25.03,121.0 Asia/Taipei 480m", encode2018(Location(25.03, 121.0, "Asia/Taipei", 480)))
      assertEquals(
        "25.03,121.0 Asia/Taipei 480m 500.0",
        encode2018(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0))
      ) // 高度 500米

      assertEquals("40.0,73.0", encode2018(Location(40.0, 73.0)))
      assertEquals("40.0,73.0 America/New_York", encode2018(Location(40.0, 73.0, "America/New_York")))
      assertEquals("40.0,73.0 America/New_York -300m", encode2018(Location(40.0, 73.0, "America/New_York", -300)))
      assertEquals(
        "40.0,73.0 America/New_York -300m 800.0",
        encode2018(Location(40.0, 73.0, "America/New_York", -300, 800.0))
      ) // 高度 800米
    }
  }

  @Test
  fun testDecode2018() {
    LocationTools.run {
      // 後方 時區、時差、高度 六種排列順序不影響結果
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 Asia/Taipei 480m 500.0"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 Asia/Taipei 500.0 480m"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 500.0 480m Asia/Taipei"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 500.0 Asia/Taipei 480m"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 480m 500.0 Asia/Taipei"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480, 500.0), decode("25.03,121.0 480m Asia/Taipei 500.0"))

      // 南緯、西經
      assertEquals(
        Location(-25.03, -121.0, "Asia/Taipei", -480, 500.0),
        decode("-25.03,-121.0 -480m Asia/Taipei 500.0")
      )

      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480), decode("25.03,121.0 Asia/Taipei 480m"))
      assertEquals(Location(25.03, 121.0), decode("25.03,121.0"))
    }
  }

  @Test
  fun testDecode2018_時區_時差() {
    LocationTools.run {
      // 後方 時區、時差 2種排列順序不影響結果
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480), decode("25.03,121.0 Asia/Taipei 480m"))
      assertEquals(Location(25.03, 121.0, "Asia/Taipei", 480), decode("25.03,121.0 480m Asia/Taipei"))
    }
  }

  @Test
  fun testDecode2018_只有時區() {
    LocationTools.run {
      assertEquals(Location(25.03, 121.0, "Asia/Taipei"), decode("25.03,121.0 Asia/Taipei"))
    }
  }

  @Test
  fun justTest_隱碼() {
    LocationTools.run {
      assertEquals(Location(25.0, 121.0, "Asia/Taipei", 480), decode("25.0,121.0 Asia/Taipei 480m"))
      assertEquals(
        Location(25.0, 121.0, "Asia/Taipei", 480, 100.0),
        decode("25.0,121.0 Asia/Taipei 480m 100.0\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F\u000F")
      )
      assertEquals(
        Location(25.0, 121.0, "Asia/Taipei", 480),
        decode("25.0,121.0 Asia/Taipei 480m\u0005\u0005\u0005\u0005\u0005")
      )
    }
  }


  /**
   * 2012/03 之後的格式 , 尾方為 altitude TimeZone [minuteOffset]
   */
  @Test
  fun testLocationFromDebugString_format2012() {
    var location: ILocation

    location = LocationTools.decode("+1213012.44+25 312.44 12.3456 Asia/Taipei")
    assertEquals(Location(EAST, 121, 30, 12.44, NORTH, 25, 3, 12.44, "Asia/Taipei", null, 12.3456), location)

    //強制設定 minuteOffset = 0
    location = LocationTools.decode(LocationTools.encode2012(location) + " 0")
    assertEquals(Location(EAST, 121, 30, 12.44, NORTH, 25, 3, 12.44, "Asia/Taipei", 0, 12.3456), location)

    location = LocationTools.decode("+1213012.34+25 312.34 12.3456 Asia/Taipei 60")
    assertEquals(Location(EAST, 121, 30, 12.34, NORTH, 25, 3, 12.34, "Asia/Taipei", 60, 12.3456), location)

    location = LocationTools.decode("+1213012.34+25 312.34 12.3456 Asia/Taipei -480")
    assertEquals(Location(EAST, 121, 30, 12.34, NORTH, 25, 3, 12.34, "Asia/Taipei", -480, 12.3456), location)
  }


  @Test
  fun testEncode2012() {
    var location = Location(EAST, 120, 30, 12.50, NORTH, 25, 3, 12.30, TimeTools.findZoneIdByMinutes(480)!!, null, 12.3456)
    assertEquals("+12030 12.5+25 312.30 12.3456 Etc/GMT-8", LocationTools.encode2012(location))

    location = Location(EAST, 121, 30, 12.44, NORTH, 25, 3, 12.44, TimeTools.findZoneIdByMinutes(-60)!!, null, 0.0)
    assertEquals("+1213012.44+25 312.44 0.0 Etc/GMT+1", LocationTools.encode2012(location))

    location = Location(EAST, 121, 30, 12.44, NORTH, 25, 3, 12.44, TimeTools.findZoneIdByMinutes(-60)!!, null, -1000.0)
    assertEquals("+1213012.44+25 312.44 -1000.0 Etc/GMT+1", LocationTools.encode2012(location))

    location = Location(EAST, 121, 30, 12.44, NORTH, 25, 3, 12.44, TimeTools.findZoneIdByMinutes(-60)!!, 120, -1000.0)
    assertEquals("+1213012.44+25 312.44 -1000.0 Etc/GMT+1 120", LocationTools.encode2012(location))
  }

}
