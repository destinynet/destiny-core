/*
 * @author smallufo
 * @date 2004/11/20
 * @time 上午 06:48:01
 */
package destiny.core.chinese;

import java.util.Arrays;

import junit.framework.TestCase;

public class HeavenlyStemsTest extends TestCase
{
  public void testSorting()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[]
      {HeavenlyStems.丁,HeavenlyStems.癸,HeavenlyStems.甲,HeavenlyStems.戊,HeavenlyStems.辛};
    HeavenlyStems[] expected = new HeavenlyStems[]
      {HeavenlyStems.甲,HeavenlyStems.丁,HeavenlyStems.戊,HeavenlyStems.辛,HeavenlyStems.癸};

    Arrays.sort(HSArray);
    assertTrue(Arrays.equals(expected , HSArray));
  }
  
  public void testValueOf()
  {
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.valueOf("甲"));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.valueOf("癸"));
  }

  public void testGetYinYang()
  {
    assertEquals(YinYang.陽 , HeavenlyStems.甲.getYinYang());
    assertEquals(YinYang.陰 , HeavenlyStems.乙.getYinYang());
    assertEquals(YinYang.陽 , HeavenlyStems.丙.getYinYang());
    assertEquals(YinYang.陰 , HeavenlyStems.丁.getYinYang());
    assertEquals(YinYang.陽 , HeavenlyStems.戊.getYinYang());
    assertEquals(YinYang.陰 , HeavenlyStems.己.getYinYang());
    assertEquals(YinYang.陽 , HeavenlyStems.庚.getYinYang());
    assertEquals(YinYang.陰 , HeavenlyStems.辛.getYinYang());
    assertEquals(YinYang.陽 , HeavenlyStems.壬.getYinYang());
    assertEquals(YinYang.陰 , HeavenlyStems.癸.getYinYang());
  }

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

  public void testHeavenlyStems()
  {
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems('甲'));
    assertSame(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems('甲'));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(0));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(10));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(-10));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(20));
    assertEquals(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(-20));

    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems('癸'));
    assertSame(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems('癸'));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(9));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(19));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(-1));
    assertEquals(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(-11));
  }

  public void testGetHeavenlyStems()
  {
    assertSame(HeavenlyStems.甲 , HeavenlyStems.getHeavenlyStems(FiveElement.木 , YinYang.陽));
    assertSame(HeavenlyStems.乙 , HeavenlyStems.getHeavenlyStems(FiveElement.木 , YinYang.陰));
    assertSame(HeavenlyStems.丙 , HeavenlyStems.getHeavenlyStems(FiveElement.火 , YinYang.陽));
    assertSame(HeavenlyStems.丁 , HeavenlyStems.getHeavenlyStems(FiveElement.火 , YinYang.陰));
    assertSame(HeavenlyStems.戊 , HeavenlyStems.getHeavenlyStems(FiveElement.土 , YinYang.陽));
    assertSame(HeavenlyStems.己 , HeavenlyStems.getHeavenlyStems(FiveElement.土 , YinYang.陰));
    assertSame(HeavenlyStems.庚 , HeavenlyStems.getHeavenlyStems(FiveElement.金 , YinYang.陽));
    assertSame(HeavenlyStems.辛 , HeavenlyStems.getHeavenlyStems(FiveElement.金 , YinYang.陰));
    assertSame(HeavenlyStems.壬 , HeavenlyStems.getHeavenlyStems(FiveElement.水 , YinYang.陽));
    assertSame(HeavenlyStems.癸 , HeavenlyStems.getHeavenlyStems(FiveElement.水 , YinYang.陰));
  }

  public void testSort()
  {
    HeavenlyStems[] HSArray = new HeavenlyStems[] { HeavenlyStems.丁, HeavenlyStems.癸, HeavenlyStems.甲, HeavenlyStems.戊, HeavenlyStems.辛 };
    System.out.println("排序前:");

    for (int i = 0; i < HSArray.length; i++)
      System.out.print(HSArray[i] + "\t");

    Arrays.sort(HSArray);

    System.out.println("\n排序後");
    for (int i = 0; i < HSArray.length; i++)
      System.out.print(HSArray[i] + "\t");
  }

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
