/**
 * @author smallufo
 * Created on 2007/3/14 at 上午 3:04:49
 */
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.tools.location.TimeZoneUtils;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class TimeToolsLongitudeTimeTest {

  LocalDateTime lmt;

  Location location;

  LocalDateTime expected;

  /** 東經121度30分 , 應該加六分鐘 */
  @Test
  public void testGetLocalTimeEast1() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.EAST, 121, 30, 0, NorthSouth.NORTH, 25, 03, 0, 0, "Asia/Taipei");

    expected = LocalDateTime.of(2007, 3, 14, 0, 6, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 東經 130度 , 理應在東九區，但假設仍在東八區(時差480分)的話，應該加 40分鐘 */
  @Test
  public void testGetLocalTimeEast2() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.EAST, 130, 0, 0, NorthSouth.NORTH, 25, 03, 0, 0, "Asia/Taipei");

    expected = LocalDateTime.of(2007, 3, 14, 0, 40, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 東經 115.5度 , 理應減 (120-115.5)x4 = 4.5x4 = 18分 */
  @Test
  public void testGetLocalTimeEast3() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.EAST, 115.5, NorthSouth.NORTH, 25.0, 0, "Asia/Taipei", null);
    expected = LocalDateTime.of(2007, 3, 13, 23, 42, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 東經 110度 , 理應在東七區，但假設仍在東八區(時差480分)的話，應該減 40分鐘 */
  @Test
  public void testGetLocalTimeEast4() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.EAST, 110, NorthSouth.NORTH, 25.0, 0, "Asia/Taipei", null);
    expected = LocalDateTime.of(2007, 3, 13, 23, 20, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 西經 76.5度 (西五區，負300分鐘) , 應該減 (76.5-75)x4 = 1.5x4=6分鐘 */
  @Test
  public void testGetLocalTimeWest1() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.WEST, 76.5, NorthSouth.NORTH, 25.0, 0, TimeZoneUtils.Companion.getTimeZone(-300).getID(), null);
    expected = LocalDateTime.of(2007, 3, 13, 23, 54, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 西經 85度 , 理應在西六區 , 但如果仍在西五區(時差 -300分) , 應該減 40分   */
  @Test
  public void testGetLocalTimeWest2() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.WEST, 85, NorthSouth.NORTH, 25.0, 0, TimeZoneUtils.Companion.getTimeZone(-300).getID(), null);
    expected = LocalDateTime.of(2007, 3, 13, 23, 20, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** 西經 70.5度 (西五區 , 負300分鐘) , 應該加 (75-70.5)x4 = 4.5x4=18分鐘 */
  @Test
  public void testGetLocalTimeWest3() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.WEST, 70.5, NorthSouth.NORTH, 25.0, 0, TimeZoneUtils.Companion.getTimeZone(-300).getID(), null);
    expected = LocalDateTime.of(2007, 3, 14, 0, 18, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));

  }

  /** 西經 65 度 理應在西四區 , 但如果仍在西五區(-300分)的話 , 應該加 (75-65)x4 = 40分鐘 */
  @Test
  public void testGetLocalTimeWest4() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.WEST, 65, NorthSouth.NORTH, 25.0, 0, TimeZoneUtils.Companion.getTimeZone(-300).getID(), null);
    expected = LocalDateTime.of(2007, 3, 14, 0, 40, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** GMT 時區內 , 東經5度 , 加20分 */
  @Test
  public void testGetLocalTimeEast() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.EAST, 5, NorthSouth.NORTH, 25.0, 0, "GMT", null);
    expected = LocalDateTime.of(2007, 3, 14, 0, 20, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

  /** GMT 時區內 , 西經5度 , 減20分 */
  @Test
  public void testGetLocalTimeWest() {
    lmt = LocalDateTime.of(2007, 3, 14, 0, 0, 0);
    location = new Location(EastWest.WEST, 5, NorthSouth.NORTH, 25.0, 0, "GMT", null);
    expected = LocalDateTime.of(2007, 3, 13, 23, 40, 0);
    assertEquals(expected, TimeTools.getLongitudeTime(lmt, location));
  }

}
