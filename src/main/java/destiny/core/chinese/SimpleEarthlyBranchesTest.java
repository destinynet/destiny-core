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
    assertSame(YinYang.陽 , SimpleEarthlyBranches.子.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.丑.getYinYang());
    assertSame(YinYang.陽 , SimpleEarthlyBranches.寅.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.卯.getYinYang());
    assertSame(YinYang.陽 , SimpleEarthlyBranches.辰.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.巳.getYinYang());
    assertSame(YinYang.陽 , SimpleEarthlyBranches.午.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.未.getYinYang());
    assertSame(YinYang.陽 , SimpleEarthlyBranches.申.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.酉.getYinYang());
    assertSame(YinYang.陽 , SimpleEarthlyBranches.戌.getYinYang());
    assertSame(YinYang.陰 , SimpleEarthlyBranches.亥.getYinYang());
  }

}
