/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 3:04:49
 */
package destiny.core.calendar

import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.tools.location.TimeZoneUtils
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeToolsLongitudeTimeTest {

  private val asiaTaipei = "Asia/Taipei"


  /** 東經121度30分 , 應該加六分鐘  */
  @Test
  fun testGetLocalTimeEast1() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(EAST, 121, 30, 0.0, NORTH, 25, 3, 0.0, asiaTaipei)

    val expected = LocalDateTime.of(2007, 3, 14, 0, 6, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 東經 130度 , 理應在東九區，但假設仍在東八區(時差480分)的話，應該加 40分鐘  */
  @Test
  fun testGetLocalTimeEast2() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(EAST, 130, 0, 0.0, NORTH, 25, 3, 0.0, asiaTaipei)

    val expected = LocalDateTime.of(2007, 3, 14, 0, 40, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 東經 115.5度 , 理應減 (120-115.5)x4 = 4.5x4 = 18分  */
  @Test
  fun testGetLocalTimeEast3() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(EAST, 115.5, NORTH, 25.0, asiaTaipei)
    val expected = LocalDateTime.of(2007, 3, 13, 23, 42, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 東經 110度 , 理應在東七區，但假設仍在東八區(時差480分)的話，應該減 40分鐘  */
  @Test
  fun testGetLocalTimeEast4() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(EAST, 110.0, NORTH, 25.0, asiaTaipei)
    val expected = LocalDateTime.of(2007, 3, 13, 23, 20, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 西經 76.5度 (西五區，負300分鐘) , 應該減 (76.5-75)x4 = 1.5x4=6分鐘  */
  @Test
  fun testGetLocalTimeWest1() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(WEST, 76.5, NORTH, 25.0, TimeZoneUtils.getTimeZone(-300).id)
    val expected = LocalDateTime.of(2007, 3, 13, 23, 54, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 西經 85度 , 理應在西六區 , 但如果仍在西五區(時差 -300分) , 應該減 40分    */
  @Test
  fun testGetLocalTimeWest2() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(WEST, 85.0, NORTH, 25.0, TimeZoneUtils.getTimeZone(-300).id)
    val expected = LocalDateTime.of(2007, 3, 13, 23, 20, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** 西經 70.5度 (西五區 , 負300分鐘) , 應該加 (75-70.5)x4 = 4.5x4=18分鐘  */
  @Test
  fun testGetLocalTimeWest3() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(WEST, 70.5, NORTH, 25.0, TimeZoneUtils.getTimeZone(-300).id, null, 0.0)
    val expected = LocalDateTime.of(2007, 3, 14, 0, 18, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))

  }

  /** 西經 65 度 理應在西四區 , 但如果仍在西五區(-300分)的話 , 應該加 (75-65)x4 = 40分鐘  */
  @Test
  fun testGetLocalTimeWest4() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(WEST, 65.0, NORTH, 25.0, TimeZoneUtils.getTimeZone(-300).id, null, 0.0)
    val expected = LocalDateTime.of(2007, 3, 14, 0, 40, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** GMT 時區內 , 東經5度 , 加20分  */
  @Test
  fun testGetLocalTimeEast() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(EAST, 5.0, NORTH, 25.0, "GMT", null, 0.0)
    val expected = LocalDateTime.of(2007, 3, 14, 0, 20, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

  /** GMT 時區內 , 西經5度 , 減20分  */
  @Test
  fun testGetLocalTimeWest() {
    val lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0)
    val location = Location(WEST, 5.0, NORTH, 25.0, "GMT", null, 0.0)
    val expected = LocalDateTime.of(2007, 3, 13, 23, 40, 0)
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location))
  }

}
