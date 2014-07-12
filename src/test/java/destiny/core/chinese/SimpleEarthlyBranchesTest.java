/**
 * @author smallufo 
 * Created on 2005/7/5 at 下午 01:02:34
 */ 
package destiny.core.chinese;

import junit.framework.TestCase;

public class SimpleEarthlyBranchesTest extends TestCase
{
  public void testGetFiveElement()
  {
    assertSame(FiveElement.水 , SimpleEarthlyBranches.子.getFiveElement());
    assertSame(FiveElement.土 , SimpleEarthlyBranches.丑.getFiveElement());
    assertSame(FiveElement.木 , SimpleEarthlyBranches.寅.getFiveElement());
    assertSame(FiveElement.木 , SimpleEarthlyBranches.卯.getFiveElement());
    assertSame(FiveElement.土 , SimpleEarthlyBranches.辰.getFiveElement());
    assertSame(FiveElement.火 , SimpleEarthlyBranches.巳.getFiveElement());
    assertSame(FiveElement.火 , SimpleEarthlyBranches.午.getFiveElement());
    assertSame(FiveElement.土 , SimpleEarthlyBranches.未.getFiveElement());
    assertSame(FiveElement.金 , SimpleEarthlyBranches.申.getFiveElement());
    assertSame(FiveElement.金 , SimpleEarthlyBranches.酉.getFiveElement());
    assertSame(FiveElement.土 , SimpleEarthlyBranches.戌.getFiveElement());
    assertSame(FiveElement.水 , SimpleEarthlyBranches.亥.getFiveElement());
  }

  public void testGetFiveElementEarthlyBranches()
  {
    assertSame(FiveElement.水 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.子));
    assertSame(FiveElement.土 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.丑));
    assertSame(FiveElement.木 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.寅));
    assertSame(FiveElement.木 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.卯));
    assertSame(FiveElement.土 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.辰));
    assertSame(FiveElement.火 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.巳));
    assertSame(FiveElement.火 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.午));
    assertSame(FiveElement.土 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.未));
    assertSame(FiveElement.金 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.申));
    assertSame(FiveElement.金 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.酉));
    assertSame(FiveElement.土 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.戌));
    assertSame(FiveElement.水 , SimpleEarthlyBranches.getFiveElement(EarthlyBranches.亥));
  }

  public void testGetYinYang()
  {
    assertSame(true  , SimpleEarthlyBranches.子.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.丑.getBooleanValue());
    assertSame(true  , SimpleEarthlyBranches.寅.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.卯.getBooleanValue());
    assertSame(true  , SimpleEarthlyBranches.辰.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.巳.getBooleanValue());
    assertSame(true  , SimpleEarthlyBranches.午.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.未.getBooleanValue());
    assertSame(true  , SimpleEarthlyBranches.申.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.酉.getBooleanValue());
    assertSame(true  , SimpleEarthlyBranches.戌.getBooleanValue());
    assertSame(false , SimpleEarthlyBranches.亥.getBooleanValue());
  }

}
