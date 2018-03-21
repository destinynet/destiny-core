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
}