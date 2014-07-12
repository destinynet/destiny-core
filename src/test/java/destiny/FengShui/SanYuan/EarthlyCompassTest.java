/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 11:21:27
 */
package destiny.FengShui.SanYuan;

import junit.framework.TestCase;

public class EarthlyCompassTest extends TestCase
{
  public void testEarthlyCompass()
  {
    EarthlyCompass e = new EarthlyCompass();
    
    //子山開始度數 (352.5)
    assertEquals(352.5 , e.getStartDegree( Mountain.子));
    assertEquals(7.5 , e.getEndDegree(Mountain.子));
    
    //卯山開始度數 ( 82.5)
    assertEquals(82.5 , e.getStartDegree( Mountain.卯));
    assertEquals(97.5 , e.getEndDegree( Mountain.卯));
    
    //癸山開始度數 (  7.5)
    assertEquals(7.5 , e.getStartDegree( Mountain.癸));
    assertEquals(22.5 , e.getEndDegree( Mountain.癸));
    
    //午山開始度數 (172.5)
    assertEquals(172.5 , e.getStartDegree( Mountain.午));
    assertEquals(187.5 , e.getEndDegree( Mountain.午));
    
    //乾山開始度數 (307.5)
    assertEquals(307.5 , e.getStartDegree( Mountain.乾));
    assertEquals(322.5 , e.getEndDegree( Mountain.乾));
    
    
    //359度是屬於 (子) 
    assertSame(Mountain.子 , e.getMountain(359));
    //  0度是屬於 (子)
    assertSame(Mountain.子 , e.getMountain(0));
    //  9度是屬於 (癸)
    assertSame(Mountain.癸 , e.getMountain(9));
    //128度是屬於 (巽) 
    assertSame(Mountain.巽 , e.getMountain(128));
    //325度是屬於 (亥) 
    assertSame(Mountain.亥 , e.getMountain(325));
    
    assertSame(false , e.getYinYang(Mountain.子));
    assertSame(false , e.getYinYang(Mountain.丑));
    //寅(陽) 
    assertSame(true  , e.getYinYang(Mountain.寅));
    assertSame(false , e.getYinYang(Mountain.卯));
    assertSame(false , e.getYinYang(Mountain.辰));
    //巳(陽)
    assertSame(true  , e.getYinYang(Mountain.巳));
    assertSame(false , e.getYinYang(Mountain.午));
    assertSame(false , e.getYinYang(Mountain.未));


    assertSame(true  , e.getYinYang(Mountain.乾));
    assertSame(true  , e.getYinYang(Mountain.坤));
    assertSame(true  , e.getYinYang(Mountain.艮));
    assertSame(true  , e.getYinYang(Mountain.巽));
    
    assertSame(true  , e.getYinYang(Mountain.甲));
    assertSame(false , e.getYinYang(Mountain.乙));
    assertSame(true  , e.getYinYang(Mountain.丙));
    assertSame(false , e.getYinYang(Mountain.丁));
    assertSame(true  , e.getYinYang(Mountain.庚));
    assertSame(false , e.getYinYang(Mountain.辛));
    assertSame(true  , e.getYinYang(Mountain.壬));
    assertSame(false , e.getYinYang(Mountain.癸));
  }
}
