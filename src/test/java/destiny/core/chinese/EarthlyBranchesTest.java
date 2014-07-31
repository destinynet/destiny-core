/*
 * @author smallufo
 * Created on 2004/11/20 at 上午 07:13:48
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class EarthlyBranchesTest
{
  @Test
  public void testGetEarthlyBranchesFromInt()
  {
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(-1));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(0));
    assertSame(EarthlyBranches.丑 , EarthlyBranches.getEarthlyBranches(1));
    assertSame(EarthlyBranches.寅 , EarthlyBranches.getEarthlyBranches(2));
    assertSame(EarthlyBranches.卯 , EarthlyBranches.getEarthlyBranches(3));
    assertSame(EarthlyBranches.辰 , EarthlyBranches.getEarthlyBranches(4));
    assertSame(EarthlyBranches.巳 , EarthlyBranches.getEarthlyBranches(5));
    assertSame(EarthlyBranches.午 , EarthlyBranches.getEarthlyBranches(6));
    assertSame(EarthlyBranches.未 , EarthlyBranches.getEarthlyBranches(7));
    assertSame(EarthlyBranches.申 , EarthlyBranches.getEarthlyBranches(8));
    assertSame(EarthlyBranches.酉 , EarthlyBranches.getEarthlyBranches(9));
    assertSame(EarthlyBranches.戌 , EarthlyBranches.getEarthlyBranches(10));
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(11));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(12));
  }

  @Test
  public void testGetEarthlyBranchesFromChar()
  {
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches('子').get());
    assertSame(EarthlyBranches.丑 , EarthlyBranches.getEarthlyBranches('丑').get());
    assertSame(EarthlyBranches.寅 , EarthlyBranches.getEarthlyBranches('寅').get());
    assertSame(EarthlyBranches.卯 , EarthlyBranches.getEarthlyBranches('卯').get());
    assertSame(EarthlyBranches.辰 , EarthlyBranches.getEarthlyBranches('辰').get());
    assertSame(EarthlyBranches.巳 , EarthlyBranches.getEarthlyBranches('巳').get());
    assertSame(EarthlyBranches.午 , EarthlyBranches.getEarthlyBranches('午').get());
    assertSame(EarthlyBranches.未 , EarthlyBranches.getEarthlyBranches('未').get());
    assertSame(EarthlyBranches.申 , EarthlyBranches.getEarthlyBranches('申').get());
    assertSame(EarthlyBranches.酉 , EarthlyBranches.getEarthlyBranches('酉').get());
    assertSame(EarthlyBranches.戌 , EarthlyBranches.getEarthlyBranches('戌').get());
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches('亥').get());
    try
    {
      assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches('無').get());
      fail("An exception should ba raised");
    }
    catch(RuntimeException e)
    {
      assertTrue(true);
    }
  }

  @Test
  public void testGetIndexStatic()
  {
    assertSame(0 , EarthlyBranches.getIndex(EarthlyBranches.子));
    assertSame(1 , EarthlyBranches.getIndex(EarthlyBranches.丑));
    assertSame(2 , EarthlyBranches.getIndex(EarthlyBranches.寅));
    assertSame(3 , EarthlyBranches.getIndex(EarthlyBranches.卯));
    assertSame(4 , EarthlyBranches.getIndex(EarthlyBranches.辰));
    assertSame(5 , EarthlyBranches.getIndex(EarthlyBranches.巳));
    assertSame(6 , EarthlyBranches.getIndex(EarthlyBranches.午));
    assertSame(7 , EarthlyBranches.getIndex(EarthlyBranches.未));
    assertSame(8 , EarthlyBranches.getIndex(EarthlyBranches.申));
    assertSame(9 , EarthlyBranches.getIndex(EarthlyBranches.酉));
    assertSame(10, EarthlyBranches.getIndex(EarthlyBranches.戌));
    assertSame(11, EarthlyBranches.getIndex(EarthlyBranches.亥));
  }

  @Test
  public void testGetIndexDynamic()
  {
    assertSame(0 , EarthlyBranches.子.getIndex());
    assertSame(1 , EarthlyBranches.丑.getIndex());
    assertSame(2 , EarthlyBranches.寅.getIndex());
    assertSame(3 , EarthlyBranches.卯.getIndex());
    assertSame(4 , EarthlyBranches.辰.getIndex());
    assertSame(5 , EarthlyBranches.巳.getIndex());
    assertSame(6 , EarthlyBranches.午.getIndex());
    assertSame(7 , EarthlyBranches.未.getIndex());
    assertSame(8 , EarthlyBranches.申.getIndex());
    assertSame(9 , EarthlyBranches.酉.getIndex());
    assertSame(10, EarthlyBranches.戌.getIndex());
    assertSame(11, EarthlyBranches.亥.getIndex());
    
  }

  @Test
  public void testSorting()
  {
    EarthlyBranches[] EBArray = new EarthlyBranches[] 
     { EarthlyBranches.午 , EarthlyBranches.酉 , EarthlyBranches.子 , EarthlyBranches.卯};
    Arrays.sort(EBArray);
    EarthlyBranches[] expected = new EarthlyBranches[] 
     { EarthlyBranches.子 , EarthlyBranches.卯 , EarthlyBranches.午 , EarthlyBranches.酉};
    assertTrue(Arrays.equals(expected , EBArray));
  }

  @Test
  public void testEarthlyBranches()
  {
    assertEquals("子" , EarthlyBranches.子.toString());
    assertEquals(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches('子').get());
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches('子').get());
    assertEquals(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(0));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(0));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(12));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(-12));
    assertSame(EarthlyBranches.子 , EarthlyBranches.getEarthlyBranches(-24));
    
    assertEquals("亥" , EarthlyBranches.亥.toString());
    assertEquals(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches('亥').get());
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches('亥').get());
    assertEquals(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(11));
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(11));
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(23));
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(-1));
    assertSame(EarthlyBranches.亥 , EarthlyBranches.getEarthlyBranches(-13));
    
  }
}

