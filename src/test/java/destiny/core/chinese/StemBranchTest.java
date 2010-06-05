/*
 * @author smallufo
 * @date 2004/11/20
 * @time 下午 05:43:54
 */
package destiny.core.chinese;

import java.util.Arrays;

import junit.framework.TestCase;

public class StemBranchTest extends TestCase
{
  public void testStemBranch()
  {
    StemBranch sb1 = StemBranch.get('甲','子');
    
    StemBranch sb3 = StemBranch.get(0);
    assertSame(sb1 , sb3);
    
    StemBranch sb4 = StemBranch.get(0);
    assertSame(sb1 , sb4);
    
    assertSame(sb3 , sb4);
    
    StemBranch sb5 = StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.子);
    assertSame(sb4 , sb5);
    
    StemBranch sb6 = StemBranch.get('甲' , '子');
    assertSame(sb4 , sb6);
    assertSame(sb5, sb6);
    
    StemBranch sb7 = StemBranch.get("甲子");
    assertSame(sb4 , sb7);
    assertSame(sb5 , sb7);
    assertSame(sb6 , sb7);
  }
  
  public void testSorting()
  {
    StemBranch[] SBArray1 = new StemBranch[]
      { StemBranch.get(10),
        StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.午),
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
  
  public void testGetNext()
  {
    StemBranch sb = StemBranch.get("甲子");
    assertSame(sb.getNext() , StemBranch.get("乙丑"));
    
    sb = StemBranch.get("癸亥");
    assertSame(sb.getNext() , StemBranch.get("甲子"));
  }
  
  public void testGetPrevious()
  {
    StemBranch sb = StemBranch.get("甲子");
    assertSame(sb.getPrevious() , StemBranch.get("癸亥"));
    
    sb = StemBranch.get("乙丑");
    assertSame(sb.getPrevious() , StemBranch.get("甲子"));
  }
}
