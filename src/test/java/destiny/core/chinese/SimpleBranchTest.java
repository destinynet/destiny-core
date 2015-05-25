/**
 * @author smallufo 
 * Created on 2005/7/5 at 下午 01:02:34
 */ 
package destiny.core.chinese;

import org.junit.Test;

import static destiny.core.chinese.SimpleBranch.*;
import static org.junit.Assert.assertSame;

public class SimpleBranchTest
{
  @Test
  public void testGet() {
    assertSame(SimpleBranch.子, SimpleBranch.get(Branch.子));
  }

  @Test
  public void testGetFiveElement()
  {
    assertSame(FiveElement.水 , 子.getFiveElement());
    assertSame(FiveElement.土 , 丑.getFiveElement());
    assertSame(FiveElement.木 , 寅.getFiveElement());
    assertSame(FiveElement.木 , 卯.getFiveElement());
    assertSame(FiveElement.土 , 辰.getFiveElement());
    assertSame(FiveElement.火 , 巳.getFiveElement());
    assertSame(FiveElement.火 , 午.getFiveElement());
    assertSame(FiveElement.土 , 未.getFiveElement());
    assertSame(FiveElement.金 , 申.getFiveElement());
    assertSame(FiveElement.金 , 酉.getFiveElement());
    assertSame(FiveElement.土 , 戌.getFiveElement());
    assertSame(FiveElement.水 , 亥.getFiveElement());
  }

  @Test
  public void testGetFiveElementEarthlyBranches()
  {
    assertSame(FiveElement.水 , getFiveElement(Branch.子));
    assertSame(FiveElement.土 , getFiveElement(Branch.丑));
    assertSame(FiveElement.木 , getFiveElement(Branch.寅));
    assertSame(FiveElement.木 , getFiveElement(Branch.卯));
    assertSame(FiveElement.土 , getFiveElement(Branch.辰));
    assertSame(FiveElement.火 , getFiveElement(Branch.巳));
    assertSame(FiveElement.火 , getFiveElement(Branch.午));
    assertSame(FiveElement.土 , getFiveElement(Branch.未));
    assertSame(FiveElement.金 , getFiveElement(Branch.申));
    assertSame(FiveElement.金 , getFiveElement(Branch.酉));
    assertSame(FiveElement.土 , getFiveElement(Branch.戌));
    assertSame(FiveElement.水 , getFiveElement(Branch.亥));
  }

  @Test
  public void testGetYinYang()
  {
    assertSame(true  , 子.getBooleanValue());
    assertSame(false , 丑.getBooleanValue());
    assertSame(true  , 寅.getBooleanValue());
    assertSame(false , 卯.getBooleanValue());
    assertSame(true  , 辰.getBooleanValue());
    assertSame(false , 巳.getBooleanValue());
    assertSame(true  , 午.getBooleanValue());
    assertSame(false , 未.getBooleanValue());
    assertSame(true  , 申.getBooleanValue());
    assertSame(false , 酉.getBooleanValue());
    assertSame(true  , 戌.getBooleanValue());
    assertSame(false , 亥.getBooleanValue());
  }

}
