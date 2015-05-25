/*
 * @author smallufo
 * Created on 2004/11/20 at 上午 07:13:48
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static destiny.core.chinese.Branch.*;
import static org.junit.Assert.*;

public class BranchTest
{

  @Test
  public void testGetAheadOf() {
    assertSame(0  , 子.getAheadOf(子));
    assertSame(1  , 子.getAheadOf(亥));
    assertSame(2  , 子.getAheadOf(戌));
    assertSame(3  , 子.getAheadOf(酉));
    assertSame(4  , 子.getAheadOf(申));
    assertSame(5  , 子.getAheadOf(未));
    assertSame(6  , 子.getAheadOf(午));
    assertSame(7  , 子.getAheadOf(巳));
    assertSame(8  , 子.getAheadOf(辰));
    assertSame(9  , 子.getAheadOf(卯));
    assertSame(10 , 子.getAheadOf(寅));
    assertSame(11 , 子.getAheadOf(丑));

    assertSame(0  , 亥.getAheadOf(亥));
    assertSame(1  , 亥.getAheadOf(戌));
    assertSame(2  , 亥.getAheadOf(酉));
    assertSame(3  , 亥.getAheadOf(申));
    assertSame(4  , 亥.getAheadOf(未));
    assertSame(5  , 亥.getAheadOf(午));
    assertSame(6  , 亥.getAheadOf(巳));
    assertSame(7  , 亥.getAheadOf(辰));
    assertSame(8  , 亥.getAheadOf(卯));
    assertSame(9  , 亥.getAheadOf(寅));
    assertSame(10 , 亥.getAheadOf(丑));
    assertSame(11 , 亥.getAheadOf(子));
  }

  @Test
  public void testNext() {
    assertSame(子 , 子.next(0));
    assertSame(丑 , 子.next(1));
    assertSame(亥 , 子.next(11));
    assertSame(子 , 子.next(12));
    assertSame(子 , 子.next(120));

    assertSame(亥 , 亥.next(0));
    assertSame(子 , 亥.next(1));
    assertSame(戌 , 亥.next(11));
    assertSame(亥 , 亥.next(12));
    assertSame(亥 , 亥.next(120));
  }

  @Test
  public void testPrev() {
    assertSame(子 , 子.prev(0));
    assertSame(亥 , 子.prev(1));
    assertSame(丑 , 子.prev(11));
    assertSame(子 , 子.prev(12));
    assertSame(子 , 子.prev(120));

    assertSame(亥 , 亥.prev(0));
    assertSame(戌 , 亥.prev(1));
    assertSame(子 , 亥.prev(11));
    assertSame(亥 , 亥.prev(12));
    assertSame(亥 , 亥.prev(120));

  }


  @Test
  public void testGetEarthlyBranchesFromInt()
  {
    assertSame(亥 , getEarthlyBranches(-1));
    assertSame(子 , getEarthlyBranches(0));
    assertSame(丑 , getEarthlyBranches(1));
    assertSame(寅 , getEarthlyBranches(2));
    assertSame(卯 , getEarthlyBranches(3));
    assertSame(辰 , getEarthlyBranches(4));
    assertSame(巳 , getEarthlyBranches(5));
    assertSame(午 , getEarthlyBranches(6));
    assertSame(未 , getEarthlyBranches(7));
    assertSame(申 , getEarthlyBranches(8));
    assertSame(酉 , getEarthlyBranches(9));
    assertSame(戌 , getEarthlyBranches(10));
    assertSame(亥 , getEarthlyBranches(11));
    assertSame(子 , getEarthlyBranches(12));
  }

  @Test
  public void testGetEarthlyBranchesFromChar()
  {
    assertSame(子 , getEarthlyBranches('子').get());
    assertSame(丑 , getEarthlyBranches('丑').get());
    assertSame(寅 , getEarthlyBranches('寅').get());
    assertSame(卯 , getEarthlyBranches('卯').get());
    assertSame(辰 , getEarthlyBranches('辰').get());
    assertSame(巳 , getEarthlyBranches('巳').get());
    assertSame(午 , getEarthlyBranches('午').get());
    assertSame(未 , getEarthlyBranches('未').get());
    assertSame(申 , getEarthlyBranches('申').get());
    assertSame(酉 , getEarthlyBranches('酉').get());
    assertSame(戌 , getEarthlyBranches('戌').get());
    assertSame(亥 , getEarthlyBranches('亥').get());
    try
    {
      assertSame(亥 , getEarthlyBranches('無').get());
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
    assertSame(0 , getIndex(子));
    assertSame(1 , getIndex(丑));
    assertSame(2 , getIndex(寅));
    assertSame(3 , getIndex(卯));
    assertSame(4 , getIndex(辰));
    assertSame(5 , getIndex(巳));
    assertSame(6 , getIndex(午));
    assertSame(7 , getIndex(未));
    assertSame(8 , getIndex(申));
    assertSame(9 , getIndex(酉));
    assertSame(10, getIndex(戌));
    assertSame(11, getIndex(亥));
  }

  @Test
  public void testGetIndexDynamic()
  {
    assertSame(0 , 子.getIndex());
    assertSame(1 , 丑.getIndex());
    assertSame(2 , 寅.getIndex());
    assertSame(3 , 卯.getIndex());
    assertSame(4 , 辰.getIndex());
    assertSame(5 , 巳.getIndex());
    assertSame(6 , 午.getIndex());
    assertSame(7 , 未.getIndex());
    assertSame(8 , 申.getIndex());
    assertSame(9 , 酉.getIndex());
    assertSame(10, 戌.getIndex());
    assertSame(11, 亥.getIndex());
    
  }

  @Test
  public void testSorting()
  {
    Branch[] EBArray = new Branch[]
     { 午 , 酉 , 子 , 卯};
    Arrays.sort(EBArray);
    Branch[] expected = new Branch[]
     { 子 , 卯 , 午 , 酉};
    assertTrue(Arrays.equals(expected , EBArray));
  }

  @Test
  public void testEarthlyBranches()
  {
    assertEquals("子" , 子.toString());
    assertEquals(子 , getEarthlyBranches('子').get());
    assertSame(子 , getEarthlyBranches('子').get());
    assertEquals(子 , getEarthlyBranches(0));
    assertSame(子 , getEarthlyBranches(0));
    assertSame(子 , getEarthlyBranches(12));
    assertSame(子 , getEarthlyBranches(-12));
    assertSame(子 , getEarthlyBranches(-24));
    
    assertEquals("亥" , 亥.toString());
    assertEquals(亥 , getEarthlyBranches('亥').get());
    assertSame(亥 , getEarthlyBranches('亥').get());
    assertEquals(亥 , getEarthlyBranches(11));
    assertSame(亥 , getEarthlyBranches(11));
    assertSame(亥 , getEarthlyBranches(23));
    assertSame(亥 , getEarthlyBranches(-1));
    assertSame(亥 , getEarthlyBranches(-13));
    
  }
}

