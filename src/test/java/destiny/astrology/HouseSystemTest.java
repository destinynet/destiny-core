/**
 * @author smallufo 
 * Created on 2007/6/24 at 上午 2:22:48
 */ 
package destiny.astrology;

import java.util.Locale;

import junit.framework.TestCase;

public class HouseSystemTest extends TestCase
{
  public void testHouseSystem()
  {
    for (HouseSystem each : HouseSystem.values())
    {
      assertNotNull(each.toString());
      assertNotSame('!' , each.toString().charAt(0));
      
      Locale locale = Locale.ENGLISH;
      assertNotNull(each.toString(locale));
      assertNotSame('!' , each.toString(locale).charAt(0));
    }
  }
}
