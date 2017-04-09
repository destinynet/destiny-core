/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:48:01
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static destiny.core.chinese.Stem.*;
import static org.junit.Assert.*;


public class StemTest
{
  @Test
  public void testGetAheadOf() {
    assertSame(0 , 甲.getAheadOf(甲));
    assertSame(1 , 甲.getAheadOf(癸));
    assertSame(2 , 甲.getAheadOf(壬));
    assertSame(3 , 甲.getAheadOf(辛));
    assertSame(4 , 甲.getAheadOf(庚));
    assertSame(5 , 甲.getAheadOf(己));
    assertSame(6 , 甲.getAheadOf(戊));
    assertSame(7 , 甲.getAheadOf(丁));
    assertSame(8 , 甲.getAheadOf(丙));
    assertSame(9 , 甲.getAheadOf(乙));


    assertSame(0, 癸.getAheadOf(癸));
    assertSame(1, 癸.getAheadOf(壬));
    assertSame(2, 癸.getAheadOf(辛));
    assertSame(3, 癸.getAheadOf(庚));
    assertSame(4, 癸.getAheadOf(己));
    assertSame(5, 癸.getAheadOf(戊));
    assertSame(6, 癸.getAheadOf(丁));
    assertSame(7, 癸.getAheadOf(丙));
    assertSame(8, 癸.getAheadOf(乙));
    assertSame(9, 癸.getAheadOf(甲));
  }

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
    Stem[] HSArray = new Stem[]
      {丁, 癸, 甲, 戊, 辛};
    Stem[] expected = new Stem[]
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
  public void testHeavenlyStems() {
    assertEquals(甲 , Stem.get('甲').get());
    assertSame(甲   , Stem.get('甲').get());


    assertSame(甲 , get(-20));
    assertSame(甲 , get(-10));
    assertSame(乙 , get(-9));
    assertSame(壬 , get(-2));
    assertSame(癸 , get(-1));
    assertSame(甲 , get(0));
    assertSame(乙 , get(1));
    assertSame(丙 , get(2));
    assertSame(丁 , get(3));
    assertSame(戊 , get(4));
    assertSame(己 , get(5));
    assertSame(庚 , get(6));
    assertSame(辛 , get(7));
    assertSame(壬 , get(8));
    assertSame(癸 , get(9));
    assertSame(甲 , get(10));
    assertSame(甲 , get(20));


    assertEquals(癸 , Stem.get('癸').get());
    assertSame(癸   , Stem.get('癸').get());
    assertEquals(癸 , get(9));
    assertEquals(癸 , get(19));
    assertEquals(癸 , get(-1));
    assertEquals(癸 , get(-11));
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
    Stem[] HSArray = new Stem[] { 丁, 癸, 甲, 戊, 辛 };
    System.out.println("排序前:");

    for (Stem aHSArray1 : HSArray) {
      System.out.print(aHSArray1 + "\t");
    }

    Arrays.sort(HSArray);

    System.out.println("\n排序後");
    for (Stem aHSArray : HSArray) {
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
