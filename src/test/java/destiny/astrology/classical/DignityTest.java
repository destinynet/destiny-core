/**
 * @author smallufo 
 * Created on 2007/11/26 at 上午 6:11:48
 */ 
package destiny.astrology.classical;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class DignityTest
{
  @Test
  public void testDignity()
  {
    assertEquals("旺" , Dignity.RULER.toString(Locale.TAIWAN));
    assertEquals("廟" , Dignity.EXALTATION.toString(Locale.TAIWAN));
    assertEquals("陷" , Dignity.DETRIMENT.toString(Locale.TAIWAN));
    assertEquals("落" , Dignity.FALL.toString(Locale.TAIWAN));
    
    assertEquals("Ruler" , Dignity.RULER.toString(Locale.US));
    assertEquals("Exaltation" , Dignity.EXALTATION.toString(Locale.US));
    assertEquals("Detriment" , Dignity.DETRIMENT.toString(Locale.US));
    assertEquals("Fall" , Dignity.FALL.toString(Locale.US));
    
    for(Dignity each : Dignity.values())
    {
      assertNotNull(each.toString());
      assertNotSame('!' , each.toString().charAt(0));
      
      Locale locale = Locale.US;
      assertNotNull(each.toString(locale));
      assertNotSame('!' , each.toString(locale).charAt(0));
    }
  }
}
