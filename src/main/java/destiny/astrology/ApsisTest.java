/**
 * @author smallufo 
 * Created on 2007/6/27 at 上午 3:55:35
 */ 
package destiny.astrology;

import java.util.Locale;

import junit.framework.TestCase;

public class ApsisTest extends TestCase
{
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
