/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:48:01
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


public class HeavenlyStemsTest
{
  @Test
  public void testSorting()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[]
      {HeavenlyStems.丁,HeavenlyStems.癸,HeavenlyStems.甲,HeavenlyStems.戊,HeavenlyStems.辛};
    HeavenlyStems[] expected = new HeavenlyStems[]
      {HeavenlyStems.甲,HeavenlyStems.丁,HeavenlyStems.戊,HeavenlyStems.辛,HeavenlyStems.癸};

    Arrays.sort(HSArray);
    assertTrue(Arrays.equals(expected , HSArray));
  }

  @Test
  public void testValueOf()
  {
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.valueOf("甲"));
    assertSame  (HeavenlyStems.甲 , HeavenlyStems.valueOf("甲"));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.valueOf("癸"));
  }

  @Test
  public void testGetYinYang()
  {
    assertEquals(true  , HeavenlyStems.甲.getBooleanValue());
    assertEquals(false , HeavenlyStems.乙.getBooleanValue());
    assertEquals(true  , HeavenlyStems.丙.getBooleanValue());
    assertEquals(false , HeavenlyStems.丁.getBooleanValue());
    assertEquals(true  , HeavenlyStems.戊.getBooleanValue());
    assertEquals(false , HeavenlyStems.己.getBooleanValue());
    assertEquals(true  , HeavenlyStems.庚.getBooleanValue());
    assertEquals(false , HeavenlyStems.辛.getBooleanValue());
    assertEquals(true  , HeavenlyStems.壬.getBooleanValue());
    assertEquals(false , HeavenlyStems.癸.getBooleanValue());
  }

  @Test
  public void testGetFiveElements()
  {
    assertEquals(FiveElement.木 , HeavenlyStems.甲.getFiveElement());
    assertEquals(FiveElement.木 , HeavenlyStems.乙.getFiveElement());
    assertEquals(FiveElement.火 , HeavenlyStems.丙.getFiveElement());
    assertEquals(FiveElement.火 , HeavenlyStems.丁.getFiveElement());
    assertEquals(FiveElement.土 , HeavenlyStems.戊.getFiveElement());
    assertEquals(FiveElement.土 , HeavenlyStems.己.getFiveElement());
    assertEquals(FiveElement.金 , HeavenlyStems.庚.getFiveElement());
    assertEquals(FiveElement.金 , HeavenlyStems.辛.getFiveElement());
    assertEquals(FiveElement.水 , HeavenlyStems.壬.getFiveElement());
    assertEquals(FiveElement.水 , HeavenlyStems.癸.getFiveElement());
  }

  @Test
  public void testHeavenlyStems()
  {
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems('甲').get());
    assertSame(HeavenlyStems.甲   , HeavenlyStems.getHeavenlyStems('甲').get());
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(0));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(10));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(-10));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(20));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(-20));

    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems('癸').get());
    assertSame(HeavenlyStems.癸   , HeavenlyStems.getHeavenlyStems('癸').get());
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(9));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(19));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(-1));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(-11));
  }

  @Test
  public void testGetHeavenlyStems()
  {
    assertSame(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(FiveElement.木 , true));
    assertSame(HeavenlyStems.乙 , HeavenlyStems.getHeavenlyStems(FiveElement.木 , false));
    assertSame(HeavenlyStems.丙 , HeavenlyStems.getHeavenlyStems(FiveElement.火 , true));
    assertSame(HeavenlyStems.丁 , HeavenlyStems.getHeavenlyStems(FiveElement.火 , false));
    assertSame(HeavenlyStems.戊 , HeavenlyStems.getHeavenlyStems(FiveElement.土 , true));
    assertSame(HeavenlyStems.己 , HeavenlyStems.getHeavenlyStems(FiveElement.土 , false));
    assertSame(HeavenlyStems.庚 , HeavenlyStems.getHeavenlyStems(FiveElement.金 , true));
    assertSame(HeavenlyStems.辛 , HeavenlyStems.getHeavenlyStems(FiveElement.金 , false));
    assertSame(HeavenlyStems.壬 , HeavenlyStems.getHeavenlyStems(FiveElement.水 , true));
    assertSame(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(FiveElement.水 , false));
  }

  @Test
  public void testSort()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[] { HeavenlyStems.丁, HeavenlyStems.癸, HeavenlyStems.甲, HeavenlyStems.戊, HeavenlyStems.辛 };
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
    System.out.println(HeavenlyStems.甲.hashCode());
    System.out.println(HeavenlyStems.乙.hashCode());
    System.out.println(HeavenlyStems.丙.hashCode());
    System.out.println(HeavenlyStems.丁.hashCode());
    System.out.println(HeavenlyStems.戊.hashCode());
    System.out.println(HeavenlyStems.己.hashCode());
    System.out.println(HeavenlyStems.庚.hashCode());
    System.out.println(HeavenlyStems.辛.hashCode());
    System.out.println(HeavenlyStems.壬.hashCode());
    System.out.println(HeavenlyStems.癸.hashCode());
  }
}
