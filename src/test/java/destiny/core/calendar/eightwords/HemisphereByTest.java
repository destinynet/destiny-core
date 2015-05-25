/**
 * @author smallufo 
 * Created on 2008/1/27 at 上午 2:38:44
 */ 
package destiny.core.calendar.eightwords;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class HemisphereByTest
{
  @Test
  public void testHemisphereBy()
  {
    for(HemisphereBy each : HemisphereBy.values())
    {
      assertNotNull(each.toString());
      assertNotSame('!' , each.toString().charAt(0));
      
      Locale locale = Locale.ENGLISH;
      assertNotNull(each.toString(locale));
      assertNotSame('!' , each.toString(locale).charAt(0));
      
    }
  }
}
