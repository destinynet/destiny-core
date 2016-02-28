/**
 * @author smallufo
 * Created on 2010/6/23 at 下午7:32:33
 */
package destiny.iching;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class HexagramTest
{
  @Test
  public void testGetTuple() {
    List<Integer> list;
    Pair<HexagramIF, HexagramIF> tuple;
    list = Arrays.asList(7, 7, 7, 7, 7, 7);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.乾, tuple.getLeft());
    assertEquals(Hexagram.乾, tuple.getRight());

    list = Arrays.asList(9, 9, 9, 9, 9, 9);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.乾, tuple.getLeft());
    assertEquals(Hexagram.坤, tuple.getRight());

    list = Arrays.asList(9, 9, 9, 6, 6, 6);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.泰, tuple.getLeft());
    assertEquals(Hexagram.否, tuple.getRight());

    list = Arrays.asList(6, 6, 6, 9, 9, 9);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.否, tuple.getLeft());
    assertEquals(Hexagram.泰, tuple.getRight());

    list = Arrays.asList(6, 9 , 6 , 9 , 6 , 9);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.未濟, tuple.getLeft());
    assertEquals(Hexagram.既濟, tuple.getRight());

    list = Arrays.asList(9 , 6 , 9 , 6 , 9 , 6);
    tuple = Hexagram.getHexagrams(list);
    assertEquals(Hexagram.既濟, tuple.getLeft());
    assertEquals(Hexagram.未濟, tuple.getRight());
  }

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
