/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 1:41:16
 */ 
package destiny.utils.random;

import junit.framework.TestCase;

public class RandomOrgImplTest extends TestCase
{
  public void testGetIntegers()
  {
    RandomIF impl = new RandomOrgImpl();
    int[] values = impl.getIntegers(100 , 1 , 10);
    for(int each : values)
      assertTrue(each >= 1 && each <=10);
  }
}
