/*
 * @author smallufo
 * @date 2004/11/20
 * @time 下午 05:43:54
 */
package destiny.core.chinese;

import org.junit.Test;

import java.util.Arrays;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static org.junit.Assert.*;

public class StemBranchTest {

  @Test
  public void testGetEmpties() {
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 子)).contains(戌));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 子)).contains(亥));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 酉)).contains(戌));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 酉)).contains(亥));
    assertTrue(StemBranch.get(甲 , 子).getEmpties().contains(戌));
    assertTrue(StemBranch.get(甲 , 子).getEmpties().contains(亥));
    assertTrue(StemBranch.get(癸 , 酉).getEmpties().contains(戌));
    assertTrue(StemBranch.get(癸 , 酉).getEmpties().contains(亥));

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 戌)).contains(申));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 戌)).contains(酉));
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 午)).contains(申));
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 午)).contains(酉));
    assertTrue(StemBranch.get(甲 , 戌).getEmpties().contains(申));
    assertTrue(StemBranch.get(甲 , 戌).getEmpties().contains(酉));
    assertTrue(StemBranch.get(壬 , 午).getEmpties().contains(申));
    assertTrue(StemBranch.get(壬 , 午).getEmpties().contains(酉));

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 申)).contains(午));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 申)).contains(未));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 巳)).contains(午));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 巳)).contains(未));
    assertTrue(StemBranch.get(甲 , 申).getEmpties().contains(午));
    assertTrue(StemBranch.get(甲 , 申).getEmpties().contains(未));
    assertTrue(StemBranch.get(癸 , 巳).getEmpties().contains(午));
    assertTrue(StemBranch.get(癸 , 巳).getEmpties().contains(未));

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 午)).contains(辰));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 午)).contains(巳));
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 寅)).contains(辰));
    assertTrue(StemBranch.getEmpties(StemBranch.get(壬, 寅)).contains(巳));
    assertTrue(StemBranch.get(甲 , 午).getEmpties().contains(辰));
    assertTrue(StemBranch.get(甲 , 午).getEmpties().contains(巳));
    assertTrue(StemBranch.get(壬 , 寅).getEmpties().contains(辰));
    assertTrue(StemBranch.get(壬 , 寅).getEmpties().contains(巳));

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 辰)).contains(寅));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 辰)).contains(卯));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 丑)).contains(寅));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 丑)).contains(卯));
    assertTrue(StemBranch.get(甲 , 辰).getEmpties().contains(寅));
    assertTrue(StemBranch.get(甲 , 辰).getEmpties().contains(卯));
    assertTrue(StemBranch.get(癸 , 丑).getEmpties().contains(寅));
    assertTrue(StemBranch.get(癸 , 丑).getEmpties().contains(卯));

    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 寅)).contains(子));
    assertTrue(StemBranch.getEmpties(StemBranch.get(甲, 寅)).contains(丑));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 亥)).contains(子));
    assertTrue(StemBranch.getEmpties(StemBranch.get(癸, 亥)).contains(丑));
    assertTrue(StemBranch.get(甲 , 寅).getEmpties().contains(子));
    assertTrue(StemBranch.get(甲 , 寅).getEmpties().contains(丑));
    assertTrue(StemBranch.get(癸 , 亥).getEmpties().contains(子));
    assertTrue(StemBranch.get(癸 , 亥).getEmpties().contains(丑));
  }


  @Test
  public void testGetAheadOf() {
    assertSame(0 , StemBranch.get("甲子").getAheadOf(StemBranch.get("甲子")));
    assertSame(1 , StemBranch.get("甲子").getAheadOf(StemBranch.get("癸亥")));
    assertSame(59 , StemBranch.get("甲子").getAheadOf(StemBranch.get("乙丑")));

    assertSame(0 , StemBranch.get("癸亥").getAheadOf(StemBranch.get("癸亥")));
    assertSame(1 , StemBranch.get("癸亥").getAheadOf(StemBranch.get("壬戌")));
    assertSame(59 , StemBranch.get("癸亥").getAheadOf(StemBranch.get("甲子")));
  }

  @Test
  public void testNext() {
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").next(0));
    assertSame(StemBranch.get("乙丑") , StemBranch.get("甲子").next(1));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("甲子").next(59));
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").next(60));
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").next(600));

    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").next(0));
    assertSame(StemBranch.get("甲子") , StemBranch.get("癸亥").next(1));
    assertSame(StemBranch.get("壬戌") , StemBranch.get("癸亥").next(59));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").next(60));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").next(600));
  }

  @Test
  public void testPrev() {
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").prev(0));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("甲子").prev(1));
    assertSame(StemBranch.get("乙丑") , StemBranch.get("甲子").prev(59));
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").prev(60));
    assertSame(StemBranch.get("甲子") , StemBranch.get("甲子").prev(600));

    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").prev(0));
    assertSame(StemBranch.get("壬戌") , StemBranch.get("癸亥").prev(1));
    assertSame(StemBranch.get("甲子") , StemBranch.get("癸亥").prev(59));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").prev(60));
    assertSame(StemBranch.get("癸亥") , StemBranch.get("癸亥").prev(600));
  }


  @Test
  public void testGet() {
    assertSame(StemBranch.get("甲子") , StemBranch.get(甲 , 子));
    assertSame(StemBranch.get("乙丑") , StemBranch.get(Stem.乙 , 丑));
    assertSame(StemBranch.get("丙寅") , StemBranch.get(Stem.丙 , 寅));
    assertSame(StemBranch.get("丁卯") , StemBranch.get(Stem.丁 , 卯));

    try {
      StemBranch.get(甲 , 丑);
      fail();
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testStemBranch()
  {
    StemBranch sb1 = StemBranch.get('甲','子');
    
    StemBranch sb3 = StemBranch.get(0);
    assertSame(sb1 , sb3);
    
    StemBranch sb4 = StemBranch.get(0);
    assertSame(sb1 , sb4);
    
    assertSame(sb3 , sb4);
    
    StemBranch sb5 = StemBranch.get(甲 , 子);
    assertSame(sb4 , sb5);
    
    StemBranch sb6 = StemBranch.get('甲' , '子');
    assertSame(sb4 , sb6);
    assertSame(sb5, sb6);
    
    StemBranch sb7 = StemBranch.get("甲子");
    assertSame(sb4 , sb7);
    assertSame(sb5 , sb7);
    assertSame(sb6 , sb7);
  }

  @Test
  public void testSorting()
  {
    StemBranch[] SBArray1 = new StemBranch[]
      { StemBranch.get(10),
        StemBranch.get(甲 , 午),
        StemBranch.get(50),
        StemBranch.get('甲' , '子'),
        StemBranch.get(20)
      };
    StemBranch[] expected = new StemBranch[]
      { StemBranch.get('甲','子'),
        StemBranch.get('甲','戌'),
        StemBranch.get('甲','申'),
        StemBranch.get('甲','午'),
        StemBranch.get('甲','寅')
      };
    Arrays.sort(SBArray1);
    assertTrue(Arrays.equals(expected , SBArray1));
  }

  @Test
  public void testGetNext()
  {
    StemBranch sb = StemBranch.get("甲子");
    assertSame(sb.getNext() , StemBranch.get("乙丑"));
    
    sb = StemBranch.get("癸亥");
    assertSame(sb.getNext() , StemBranch.get("甲子"));
  }

  @Test
  public void testGetPrevious()
  {
    StemBranch sb = StemBranch.get("甲子");
    assertSame(sb.getPrevious() , StemBranch.get("癸亥"));
    
    sb = StemBranch.get("乙丑");
    assertSame(sb.getPrevious() , StemBranch.get("甲子"));
  }
}
