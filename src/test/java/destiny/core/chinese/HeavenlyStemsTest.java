/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:48:01
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static destiny.core.chinese.HeavenlyStems.*;
import static org.junit.Assert.*;


public class HeavenlyStemsTest
{
  @Test
  public void testNext() {
    assertSame(甲 , 甲.next(0));
    assertSame(乙 , 甲.next(1));
    assertSame(癸 , 甲.next(9));
    assertSame(甲 , 甲.next(10));
    assertSame(甲 , 甲.next(100));

    assertSame(癸 , 癸.next(0));
    assertSame(甲 , 癸.next(1));
    assertSame(壬 , 癸.next(9));
    assertSame(癸 , 癸.next(10));
    assertSame(癸 , 癸.next(100));
  }

  @Test
  public void testPrev() {
    assertSame(甲 , 甲.prev(0));
    assertSame(癸 , 甲.prev(1));
    assertSame(乙 , 甲.prev(9));
    assertSame(甲 , 甲.prev(10));
    assertSame(甲 , 甲.prev(100));

    assertSame(癸 , 癸.prev(0));
    assertSame(壬 , 癸.prev(1));
    assertSame(甲 , 癸.prev(9));
    assertSame(癸 , 癸.prev(10));
    assertSame(癸 , 癸.prev(100));
  }


  @Test
  public void testSorting()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[]
      {丁, 癸, 甲, 戊, 辛};
    HeavenlyStems[] expected = new HeavenlyStems[]
      {甲, 丁, 戊, 辛, 癸};

    Arrays.sort(HSArray);
    assertTrue(Arrays.equals(expected , HSArray));
  }

  @Test
  public void testValueOf()
  {
    assertEquals(甲 , valueOf("甲"));
    assertSame  (甲 , valueOf("甲"));
    assertEquals(癸 , valueOf("癸"));
  }

  @Test
  public void testGetYinYang()
  {
    assertEquals(true  , 甲.getBooleanValue());
    assertEquals(false , 乙.getBooleanValue());
    assertEquals(true  , 丙.getBooleanValue());
    assertEquals(false , 丁.getBooleanValue());
    assertEquals(true  , 戊.getBooleanValue());
    assertEquals(false , 己.getBooleanValue());
    assertEquals(true  , 庚.getBooleanValue());
    assertEquals(false , 辛.getBooleanValue());
    assertEquals(true  , 壬.getBooleanValue());
    assertEquals(false , 癸.getBooleanValue());
  }

  @Test
  public void testGetFiveElements()
  {
    assertEquals(FiveElement.木 , 甲.getFiveElement());
    assertEquals(FiveElement.木 , 乙.getFiveElement());
    assertEquals(FiveElement.火 , 丙.getFiveElement());
    assertEquals(FiveElement.火 , 丁.getFiveElement());
    assertEquals(FiveElement.土 , 戊.getFiveElement());
    assertEquals(FiveElement.土 , 己.getFiveElement());
    assertEquals(FiveElement.金 , 庚.getFiveElement());
    assertEquals(FiveElement.金 , 辛.getFiveElement());
    assertEquals(FiveElement.水 , 壬.getFiveElement());
    assertEquals(FiveElement.水 , 癸.getFiveElement());
  }

  @Test
  public void testHeavenlyStems()
  {
    assertEquals(甲 , getHeavenlyStems('甲').get());
    assertSame(甲   , getHeavenlyStems('甲').get());
    assertEquals(甲 , getHeavenlyStems(0));
    assertEquals(甲 , getHeavenlyStems(10));
    assertEquals(甲 , getHeavenlyStems(-10));
    assertEquals(甲 , getHeavenlyStems(20));
    assertEquals(甲 , getHeavenlyStems(-20));

    assertEquals(癸 , getHeavenlyStems('癸').get());
    assertSame(癸   , getHeavenlyStems('癸').get());
    assertEquals(癸 , getHeavenlyStems(9));
    assertEquals(癸 , getHeavenlyStems(19));
    assertEquals(癸 , getHeavenlyStems(-1));
    assertEquals(癸 , getHeavenlyStems(-11));
  }

  @Test
  public void testGetHeavenlyStems()
  {
    assertSame(甲 , getHeavenlyStems(FiveElement.木, true));
    assertSame(乙 , getHeavenlyStems(FiveElement.木, false));
    assertSame(丙 , getHeavenlyStems(FiveElement.火, true));
    assertSame(丁 , getHeavenlyStems(FiveElement.火, false));
    assertSame(戊 , getHeavenlyStems(FiveElement.土, true));
    assertSame(己 , getHeavenlyStems(FiveElement.土, false));
    assertSame(庚 , getHeavenlyStems(FiveElement.金, true));
    assertSame(辛 , getHeavenlyStems(FiveElement.金, false));
    assertSame(壬 , getHeavenlyStems(FiveElement.水, true));
    assertSame(癸 , getHeavenlyStems(FiveElement.水, false));
  }

  @Test
  public void testSort()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[] { 丁, 癸, 甲, 戊, 辛 };
    System.out.println("排序前:");

    for (HeavenlyStems aHSArray1 : HSArray) {
      System.out.print(aHSArray1 + "\t");
    }

    Arrays.sort(HSArray);

    System.out.println("\n排序後");
    for (HeavenlyStems aHSArray : HSArray) {
      System.out.print(aHSArray + "\t");
    }
  }

  @Test
  public void _testHashCode()
  {
    System.out.println(甲.hashCode());
    System.out.println(乙.hashCode());
    System.out.println(丙.hashCode());
    System.out.println(丁.hashCode());
    System.out.println(戊.hashCode());
    System.out.println(己.hashCode());
    System.out.println(庚.hashCode());
    System.out.println(辛.hashCode());
    System.out.println(壬.hashCode());
    System.out.println(癸.hashCode());
  }
}
