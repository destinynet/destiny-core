/**
 * @author smallufo
 * Created on 2011/3/11 at 上午4:09:32
 */
package destiny.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class Base58Test
{
  @Test
  public void testBase58()
  {
    assertEquals("abc123ABC" , Base58.numberToAlpha(1431117682956369L));
    
    assertEquals(1431117682956369L , Base58.alphaToNumber("abc123ABC"));
  }
}
