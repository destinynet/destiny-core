/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:43:37
 */
package destiny.core.chinese;

import junit.framework.TestCase;


public class YinYangTest extends TestCase
{
  public void testYinYang()
  {
    assertEquals("陽" , YinYang.陽.toString());
    assertEquals("陰" , YinYang.陰.toString());
    
    assertEquals(true  , YinYang.陽.getBooleanValue());
    assertEquals(false , YinYang.陰.getBooleanValue());
    
    assertNotSame(YinYang.陽 , YinYang.陰);
  }

}
