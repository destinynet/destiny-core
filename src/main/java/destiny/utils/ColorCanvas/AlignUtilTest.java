/**
 * @author smallufo 
 * Created on 2007/3/24 at 上午 12:04:18
 */ 
package destiny.utils.ColorCanvas;

import junit.framework.TestCase;

public class AlignUtilTest extends TestCase
{
  public void testAlignCenter()
  {
    int value;
    value=12;
    assertEquals("12" , AlignUtil.alignCenter(value, 2));
    assertEquals(" 12 " , AlignUtil.alignCenter(value, 4));
    assertEquals("　12　" , AlignUtil.alignCenter(value, 6));
    assertEquals("　 12　 " , AlignUtil.alignCenter(value, 8));
    assertEquals("　　12　　" , AlignUtil.alignCenter(value, 10));
    
    assertEquals(" 12" , AlignUtil.alignCenter(value, 3));
    assertEquals("　12 " , AlignUtil.alignCenter(value, 5));
    assertEquals("　 12　" , AlignUtil.alignCenter(value, 7));
    assertEquals("　　12　 " , AlignUtil.alignCenter(value, 9));
    
    
    assertEquals("　　12　　" , AlignUtil.alignCenter(value, 10));
    value=123;
    assertEquals("　 123　　" , AlignUtil.alignCenter(value, 10));
    value=1234;
    assertEquals("　 1234　 " , AlignUtil.alignCenter(value, 10));
    
    value=12;
    assertEquals("　　 12　　" , AlignUtil.alignCenter(value, 11));
    value=123;
    assertEquals("　　123　　" , AlignUtil.alignCenter(value, 11));
    value=1234;
    assertEquals("　　1234　 " , AlignUtil.alignCenter(value, 11));
    value=12345;
    assertEquals("　 12345　 " , AlignUtil.alignCenter(value, 11));
  }
  
  public void testAlignRight_1()
  {
    int value=1;
    assertEquals(" 1" , AlignUtil.alignRight(value, 2));
    assertEquals("　 1" , AlignUtil.alignRight(value, 4));
    assertEquals("　　 1" , AlignUtil.alignRight(value, 6));
  }
  
  public void testAlignRight_12()
  {
    int value=12;
    assertEquals("12" , AlignUtil.alignRight(value, 2));
    assertEquals("　12" , AlignUtil.alignRight(value, 4));
    assertEquals("　　12" , AlignUtil.alignRight(value, 6));
  }
  
  public void testAlignRight_0()
  {
    int value=0;
    assertEquals(" 0" , AlignUtil.alignRight(value, 2));
    assertEquals("　 0" , AlignUtil.alignRight(value, 4));
    assertEquals("　　 0" , AlignUtil.alignRight(value, 6));
  }
}
