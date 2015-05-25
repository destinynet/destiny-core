/**
 * @author smallufo 
 * Created on 2007/6/27 at 上午 3:55:35
 */ 
package destiny.astrology;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class ApsisTest
{
  @Test
  public void testApsis()
  {
    for (Apsis each : Apsis.values())
    {
      assertNotNull(each.toString());
      assertNotSame('!' , each.toString().charAt(0));
      
      Locale locale = Locale.US;
      assertNotNull(each.toString(locale));
      assertNotSame('!' , each.toString(locale).charAt(0));
    }
  }
}
