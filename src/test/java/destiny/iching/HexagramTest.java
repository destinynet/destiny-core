/**
 * @author smallufo
 * Created on 2010/6/23 at 下午7:32:33
 */
package destiny.iching;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

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
    Set<Hexagram> set = new HashSet<>();
    
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

  /**
   * 測試 hexagram with 多個動爻
   */
  @Test
  public void testHexagramWithMotivLines() {
    assertEquals(Hexagram.姤 , Hexagram.乾.getHexagram(1));

    assertEquals(Hexagram.遯 , Hexagram.乾.getHexagram(1 , 2));
    assertEquals(Hexagram.遯 , Hexagram.乾.getHexagram(2 , 1));

    assertEquals(Hexagram.否 , Hexagram.乾.getHexagram(1 , 2 , 3));
    assertEquals(Hexagram.否 , Hexagram.乾.getHexagram(3 , 2 , 1));
    assertEquals(Hexagram.否 , Hexagram.乾.getHexagram(2 , 3 , 1));

    assertEquals(Hexagram.觀 , Hexagram.乾.getHexagram(1 , 2 , 3 , 4));
    assertEquals(Hexagram.剝 , Hexagram.乾.getHexagram(1 , 2 , 3 , 4 , 5));
    assertEquals(Hexagram.坤 , Hexagram.乾.getHexagram(1 , 2 , 3 , 4 , 5 , 6));

    assertEquals(Hexagram.復 , Hexagram.坤.getHexagram(1));
    assertEquals(Hexagram.臨 , Hexagram.坤.getHexagram(1 , 2));
    assertEquals(Hexagram.泰 , Hexagram.坤.getHexagram(1 , 2 , 3));
    assertEquals(Hexagram.大壯 , Hexagram.坤.getHexagram(1 , 2 , 3 , 4));
    assertEquals(Hexagram.夬 , Hexagram.坤.getHexagram(1 , 2 , 3 , 4 , 5));
    assertEquals(Hexagram.乾 , Hexagram.坤.getHexagram(1 , 2 , 3 , 4 , 5, 6));
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
