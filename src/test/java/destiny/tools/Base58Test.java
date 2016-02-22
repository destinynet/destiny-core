/**
 * @author smallufo
 * Created on 2011/3/11 at 上午4:09:32
 */
package destiny.tools;

import destiny.tools.Base58;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class Base58Test
{
  @Test
  public void testBase58()
  {
    assertEquals("abc123ABC" , Base58.numberToAlpha(1431117682956369L));
    
    assertEquals(1431117682956369L , Base58.alphaToNumber("abc123ABC"));
  }
}
