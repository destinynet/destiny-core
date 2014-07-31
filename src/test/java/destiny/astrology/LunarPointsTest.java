/**
 * @author smallufo 
 * Created on 2008/6/3 at 上午 12:15:01
 */ 
package destiny.astrology;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class LunarPointsTest
{
  @Test
  public void testLunarPoints()
  {
    for(LunarPoint each : LunarPoint.values)
    {
      //System.out.println(each);
      assertNotNull(each.toString());
      assertNotSame('!' , each.toString().charAt(0));
      
      Locale locale = Locale.ENGLISH;
      assertNotNull(each.toString(locale));
      assertNotSame('!' , each.toString(locale).charAt(0));
    }
  }
}
