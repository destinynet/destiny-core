/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 2:14:27
 */ 
package destiny.utils.random;

import junit.framework.TestCase;

public class RandomImplTest extends TestCase
{
  public void testGetIntegers()
  {
    RandomIF impl = new RandomImpl();
    int[] values = impl.getIntegers(10000 , 1 , 10);
    for(int each : values)
      assertTrue(each >= 1 && each <=10);
  }
}
