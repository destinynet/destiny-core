/**
 * @author smallufo 
 * Created on 2007/3/14 at 上午 5:20:43
 */ 
package destiny.core.calendar;

import java.util.TimeZone;

import junit.framework.TestCase;
import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.utils.location.TimeZoneUtils;

public class LocationTest extends TestCase
{
  public void testNorthSouth()
  {
    assertSame(NorthSouth.NORTH , NorthSouth.getNorthSouth('N'));
    assertSame(NorthSouth.NORTH , NorthSouth.getNorthSouth('n'));
    assertSame(NorthSouth.SOUTH , NorthSouth.getNorthSouth('S'));
    assertSame(NorthSouth.SOUTH , NorthSouth.getNorthSouth('s'));
  }
  
  public void testEastWest()
  {
    assertSame(EastWest.EAST , EastWest.getEastWest('E'));
    assertSame(EastWest.EAST , EastWest.getEastWest('e'));
    assertSame(EastWest.WEST , EastWest.getEastWest('W'));
    assertSame(EastWest.WEST , EastWest.getEastWest('w'));
  }
  
  public void testLocation()
  {
    Location actual;
    Location expected;
    
    actual = new Location(-12,23,45.0 , -23,34,56.0 , TimeZoneUtils.getTimeZone(120));
    expected = new Location(EastWest.WEST , 12 , 23 , 45.0 , NorthSouth.SOUTH , 23 , 34 , 56.0 , TimeZoneUtils.getTimeZone(120));
    assertEquals(expected , actual);
    
    actual = new Location(12,23,45.0 , -23,34,56.0 , TimeZoneUtils.getTimeZone(120));
    expected = new Location(EastWest.EAST, 12 , 23 , 45.0 , NorthSouth.SOUTH , 23 , 34 , 56.0 , TimeZoneUtils.getTimeZone(120));
    assertEquals(expected , actual);
    
    
    actual = new Location(-12,23,45.0 , 23,34,56.0 , TimeZoneUtils.getTimeZone(120));
    expected = new Location(EastWest.WEST , 12 , 23 , 45.0 , NorthSouth.NORTH, 23 , 34 , 56.0 , TimeZoneUtils.getTimeZone(120));
    assertEquals(expected , actual);
    
    actual = new Location(12,23,45.0 , 23,34,56.0 , TimeZoneUtils.getTimeZone(120));
    expected = new Location(EastWest.EAST , 12 , 23 , 45.0 , NorthSouth.NORTH , 23 , 34 , 56.0 , TimeZoneUtils.getTimeZone(120));
    assertEquals(expected , actual);
  }
  
  public void testLocationDebugString()
  {
    Location location ;
    String expected ;
    location = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(480));
    expected = "+1213012.34+25 312.34 12.3456 CTT";
    assertEquals(expected , location.getDebugString());
    
    location = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(-480));
    expected = "+1213012.34+25 312.34-480 12.3456";
    
    location = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 0 , TimeZoneUtils.getTimeZone(-480));
    expected = "+1213012.34+25 312.34-480 0.0";
    
    location = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 0 , TimeZoneUtils.getTimeZone(-60));
    expected = "+1213012.34+25 312.34 0.0 Etc/GMT+1";
    assertEquals(expected , location.getDebugString());
    
    location = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , -1000 , TimeZoneUtils.getTimeZone(-60));
    expected = "+1213012.34+25 312.34 -1000.0 Etc/GMT+1";
    assertEquals(expected , location.getDebugString());
  }
  
  public void testLocationFromStaticMethod()
  {
    Location location , expected;
    
    location = Location.get("+1213012.34+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34+480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 212.34+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2  0.0+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 0.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 20.0  +25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 0.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 212.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2    0 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 0.0 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 259.99 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 59.99 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 23.0   480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZoneUtils.getTimeZone(480));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0  60 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0  60 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0  60 -1.23");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , -1.23 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);

    location = Location.get("+  1 2    9+ 1 2  3.0   0 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    
    location = Location.get("+  1 2    9+ 1 2  3.0 -60 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(-60));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0-360 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = Location.get("+  1 2    9+ 1 2  3.0-360 -1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , -1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = Location.get("-  1 2    9- 1 2  3.0-360 -1");
    expected = new Location(EastWest.WEST , 1 , 2 , 9.0 , NorthSouth.SOUTH , 1 , 2 , 3.0 , -1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34  60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34  60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34   0 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34  -0 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    
    location = Location.get("+1213012.34+25 312.34-480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(-480));
    assertEquals(expected , location);
    
    location = Location.get("+1213012.34+25 312.34 -60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(-60));
    assertEquals(expected , location);
    
  }
  
  public void testLocationFromDebugString()
  {
    Location location , expected;
    
    location = new Location("+1213012.34+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34+480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 212.34+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2  0.0+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 0.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 20.0  +25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 0.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+25 312.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 212.34 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 12.34 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2    0 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 0.0 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 259.99 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 59.99 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 23.0   480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0 480 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZone.getTimeZone("Asia/Taipei"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0  60 12.3456");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0  60 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0  60 -1.23");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , -1.23 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);

    location = new Location("+  1 2    9+ 1 2  3.0   0 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0 -60 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(-60));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0-360 1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , 1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = new Location("+  1 2    9+ 1 2  3.0-360 -1");
    expected = new Location(EastWest.EAST , 1 , 2 , 9.0 , NorthSouth.NORTH , 1 , 2 , 3.0 , -1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = new Location("-  1 2    9- 1 2  3.0-360 -1");
    expected = new Location(EastWest.WEST , 1 , 2 , 9.0 , NorthSouth.SOUTH , 1 , 2 , 3.0 , -1 , TimeZoneUtils.getTimeZone(-360));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34  60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34  60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(60));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34   0 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34  -0 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZone.getTimeZone("GMT"));
    assertEquals(expected , location);
    
    
    location = new Location("+1213012.34+25 312.34-480 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(-480));
    assertEquals(expected , location);
    
    location = new Location("+1213012.34+25 312.34 -60 12.3456");
    expected = new Location(EastWest.EAST , 121 , 30 , 12.34 , NorthSouth.NORTH , 25 , 03 , 12.34 , 12.3456 , TimeZoneUtils.getTimeZone(-60));
    assertEquals(expected , location);
    
  }
  
  public void testLocationEastWestDoubleNorthSouthDoubleInt()
  {
    Location location ;
    location = new Location(EastWest.EAST , 121.51 ,NorthSouth.NORTH , 25.33 , TimeZone.getTimeZone("Asia/Taipei")) ;
    assertEquals(121 , location.getLongitudeDegree());
    assertEquals(30 , location.getLongitudeMinute());
    assertEquals(36.0 , location.getLongitudeSecond());
    
    assertEquals(25 , location.getLatitudeDegree());
    assertEquals(19, location.getLatitudeMinute());
    assertEquals(48.0 , location.getLatitudeSecond());
  }

}
