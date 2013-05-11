/**
 * @author smallufo
 * Created on 2010/6/23 at 下午7:32:33
 */
package destiny.iching;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class HexagramTest
{
  @Test
  public void testGetHexagramLine()
  {
    Hexagram src = Hexagram.乾;
    assertSame(Hexagram.姤 , src.getHexagram(1));
  }
  
  @Test
  public void testHexagram()
  {
    Set<Hexagram> set = new HashSet<Hexagram>();
    
    for(Hexagram h :Hexagram.values())
    {
      assertTrue(!set.contains(h));
      set.add(h);
      assertNotNull(h);
      assertNotNull(h.getUpperSymbol());
      assertNotNull(h.getLowerSymbol());
    }
    assertSame(64 , set.size());
  }
  
  /** 測試互卦 */
  @Test
  public void testMiddleSpanHexagram()
  {
    assertSame(Hexagram.蹇 , Hexagram.晉.getMiddleSpanHexagram());
  }
  
  /** 測試錯卦 */
  @Test
  public void testInterlacedHexagram()
  {
    assertSame(Hexagram.需 , Hexagram.晉.getInterlacedHexagram());
  }
  
  /** 測試綜卦 */
  @Test
  public void testReversedHexagram()
  {
    assertSame(Hexagram.明夷 , Hexagram.晉.getReversedHexagram());
  }
}
