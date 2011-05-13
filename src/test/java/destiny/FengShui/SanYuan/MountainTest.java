/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 01:29:19
 */
package destiny.FengShui.SanYuan;

import junit.framework.TestCase;

public class MountainTest extends TestCase
{
  public void testMountain()
  {
    assertSame(Mountain.午 , Mountain.getOppositeMountain(Mountain.子));
    assertSame(Mountain.未 , Mountain.getOppositeMountain(Mountain.丑));
    assertSame(Mountain.申 , Mountain.getOppositeMountain(Mountain.寅));
    
    assertSame(Mountain.庚 , Mountain.getOppositeMountain(Mountain.甲));
    assertSame(Mountain.庚 , Mountain.甲.getOppositeMountain());
    
    assertSame(Mountain.巽 , Mountain.getOppositeMountain(Mountain.乾));
    assertSame(Mountain.巽 , Mountain.乾.getOppositeMountain());
  }
}
