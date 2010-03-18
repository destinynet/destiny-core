/**
 * @author smallufo 
 * Created on 2007/11/22 at 上午 3:32:28
 */ 
package destiny.astrology.classical;

import destiny.astrology.Planet;
import junit.framework.TestCase;

public class AspectEffectiveClassicalTest extends TestCase
{

  /** 測試注入 PlanetOrbsDefaultImpl 的實作 */
  public void testIsEffective_PlanetOrbsDefaultImpl()
  {
    AspectEffectiveClassical impl = new AspectEffectiveClassical();
    impl.setPlanetOrbsImpl(new PointDiameterAlBiruniImpl());
    // 日月容許度 13.5
    assertTrue (impl.isEffective(Planet.SUN, 10 , Planet.MOON, 113.5 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.SUN, 10 , Planet.MOON, 113.6 , new double[]{90}));
    assertTrue (impl.isEffective(Planet.SUN, 113.5 , Planet.MOON, 10 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.SUN, 113.6 , Planet.MOON, 10 , new double[]{90}));
    
    assertTrue (impl.isEffective(Planet.SUN, 340 , Planet.MOON, 113.5 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.SUN, 340 , Planet.MOON, 113.6 , new double[]{120}));
    assertTrue (impl.isEffective(Planet.SUN, 113.5 , Planet.MOON, 340 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.SUN, 113.6 , Planet.MOON, 340 , new double[]{120}));
    
    //月水容許度 9.5
    assertTrue (impl.isEffective(Planet.MOON, 10 , Planet.MERCURY, 90.5 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.MOON, 10 , Planet.MERCURY, 90.4 , new double[]{90}));
    assertTrue (impl.isEffective(Planet.MOON, 90.5 , Planet.MERCURY, 10 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.MOON, 90.4 , Planet.MERCURY, 10 , new double[]{90}));
    
    //水金容許度 7
    assertTrue (impl.isEffective(Planet.MERCURY, 20 , Planet.VENUS, 73 , new double[]{60}));
    assertFalse(impl.isEffective(Planet.MERCURY, 20 , Planet.VENUS, 72 , new double[]{60}));
    assertTrue (impl.isEffective(Planet.MERCURY, 87 , Planet.VENUS, 20 , new double[]{60}));
    assertFalse(impl.isEffective(Planet.MERCURY, 88 , Planet.VENUS, 20 , new double[]{60}));
    
    //金火容許度 7.5
    assertTrue (impl.isEffective(Planet.VENUS, 30 , Planet.MARS, 157.5 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.VENUS, 30 , Planet.MARS, 157.6 , new double[]{120}));
    assertTrue (impl.isEffective(Planet.VENUS, 142.5 , Planet.MARS, 30 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.VENUS, 142.4 , Planet.MARS, 30 , new double[]{120}));
    
    //火木容許度 8.5
    assertTrue (impl.isEffective(Planet.MARS, 0 , Planet.JUPITER, 278.5 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.MARS, 0 , Planet.JUPITER, 278.6 , new double[]{90}));
    assertTrue (impl.isEffective(Planet.MARS, 261.5 , Planet.JUPITER, 0 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.MARS, 261.4 , Planet.JUPITER, 0 , new double[]{90}));
    
    //木土容許度 9
    assertTrue (impl.isEffective(Planet.JUPITER, 0 , Planet.SATURN, 129 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.JUPITER, 0 , Planet.SATURN, 130 , new double[]{120}));
    assertTrue (impl.isEffective(Planet.JUPITER, 231 , Planet.SATURN, 0 , new double[]{120}));
    assertFalse(impl.isEffective(Planet.JUPITER, 230 , Planet.SATURN, 0 , new double[]{120}));
    
    //土天容許度 7
    assertTrue (impl.isEffective(Planet.SATURN, 90 , Planet.URANUS, 23 , new double[]{60}));
    assertFalse(impl.isEffective(Planet.SATURN, 90 , Planet.URANUS, 22 , new double[]{60}));
    assertTrue (impl.isEffective(Planet.SATURN, 37 , Planet.URANUS, 90 , new double[]{60}));
    assertFalse(impl.isEffective(Planet.SATURN, 38 , Planet.URANUS, 90 , new double[]{60}));
    
    //天海容許度 5
    assertTrue (impl.isEffective(Planet.URANUS, 0 , Planet.NEPTUNE, 95 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.URANUS, 0 , Planet.NEPTUNE, 96 , new double[]{90}));
    assertTrue (impl.isEffective(Planet.URANUS, 85 , Planet.NEPTUNE, 0 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.URANUS, 84 , Planet.NEPTUNE, 0 , new double[]{90}));
    
    //海冥容許度 5
    assertTrue (impl.isEffective(Planet.NEPTUNE, 0 , Planet.PLUTO, 95 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.NEPTUNE, 0 , Planet.PLUTO, 96 , new double[]{90}));
    assertTrue (impl.isEffective(Planet.NEPTUNE, 85 , Planet.PLUTO, 0 , new double[]{90}));
    assertFalse(impl.isEffective(Planet.NEPTUNE, 84 , Planet.PLUTO, 0 , new double[]{90}));
  }

}
