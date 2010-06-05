/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:59:49
 */
package destiny.core.calendar.eightwords;

import java.util.TimeZone;

import junit.framework.TestCase;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

public class MidnightLmtImplTest extends TestCase
{
  public void testGetNextMidnight()
  {
    MidnightLmtImpl impl = new MidnightLmtImpl();
    Location location = new Location(Location.EastWest.EAST , 121 , 0 , 0 , Location.NorthSouth.NORTH , 25 , 0,  0 , TimeZone.getTimeZone("Asia/Taipei"));
    Time lmt ;
    Time expected, actual;
    
    lmt = new Time(2004, 12,6,14,10,0);
    actual = impl.getNextMidnight(lmt , location);
    expected = new Time(2004, 12,7, 0,0,0);
    assertEquals(expected , actual);
    
    lmt = new Time(2004,12,31,0,0,0);
    actual = impl.getNextMidnight(lmt , location);
    expected = new Time(2005,1,1,0,0,0);
    assertEquals(expected , actual);
  }

}
