/**
 * @author smallufo
 * Created on 2010/10/10 at 下午8:08:49
 */
package destiny.test;

import org.junit.Test;

public class TestSum
{
  @Test
  public void testRun()
  {
    ISum s;
    s = new SumFactory().createSum(null, new Integer(2));
    System.out.println("Returns (null+2): "+s.sum());
   
    s = new SumFactory().createSum(new Integer(3), new Integer(2));
    System.out.println("Returns (3+2): "+s.sum());
  }
}
