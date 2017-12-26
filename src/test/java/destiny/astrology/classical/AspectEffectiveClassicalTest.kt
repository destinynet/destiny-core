/**
 * @author smallufo
 * Created on 2007/11/22 at 上午 3:32:28
 */
package destiny.astrology.classical

import destiny.astrology.Aspect
import destiny.astrology.Planet
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AspectEffectiveClassicalTest {

  /** 測試注入 PlanetOrbsDefaultImpl 的實作  */
  @Test
  fun testIsEffective_PlanetOrbsDefaultImpl() {
    val impl = AspectEffectiveClassical()
    impl.setPlanetOrbsImpl(PointDiameterAlBiruniImpl())
    // 日月容許度 13.5
    assertTrue(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, 90.0))
    assertTrue(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, Aspect.SQUARE))
    assertFalse(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.6, 90.0))
    assertFalse(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.6, Aspect.SQUARE))
    assertTrue(impl.isEffective(Planet.SUN, 113.5, Planet.MOON, 10.0, 90.0))
    assertTrue(impl.isEffective(Planet.SUN, 113.5, Planet.MOON, 10.0, Aspect.SQUARE))
    assertFalse(impl.isEffective(Planet.SUN, 113.6, Planet.MOON, 10.0, 90.0))
    assertFalse(impl.isEffective(Planet.SUN, 113.6, Planet.MOON, 10.0, Aspect.SQUARE))

    assertTrue(impl.isEffective(Planet.SUN, 340.0, Planet.MOON, 113.5, 120.0))
    assertFalse(impl.isEffective(Planet.SUN, 340.0, Planet.MOON, 113.6, 120.0))
    assertTrue(impl.isEffective(Planet.SUN, 113.5, Planet.MOON, 340.0, 120.0))
    assertFalse(impl.isEffective(Planet.SUN, 113.6, Planet.MOON, 340.0, 120.0))

    //月水容許度 9.5
    assertTrue(impl.isEffective(Planet.MOON, 10.0, Planet.MERCURY, 90.5, 90.0))
    assertFalse(impl.isEffective(Planet.MOON, 10.0, Planet.MERCURY, 90.4, 90.0))
    assertTrue(impl.isEffective(Planet.MOON, 90.5, Planet.MERCURY, 10.0, 90.0))
    assertFalse(impl.isEffective(Planet.MOON, 90.4, Planet.MERCURY, 10.0, 90.0))

    //水金容許度 7
    assertTrue(impl.isEffective(Planet.MERCURY, 20.0, Planet.VENUS, 73.0, 60.0))
    assertFalse(impl.isEffective(Planet.MERCURY, 20.0, Planet.VENUS, 72.0, 60.0))
    assertTrue(impl.isEffective(Planet.MERCURY, 87.0, Planet.VENUS, 20.0, 60.0))
    assertFalse(impl.isEffective(Planet.MERCURY, 88.0, Planet.VENUS, 20.0, 60.0))

    //金火容許度 7.5
    assertTrue(impl.isEffective(Planet.VENUS, 30.0, Planet.MARS, 157.5, 120.0))
    assertFalse(impl.isEffective(Planet.VENUS, 30.0, Planet.MARS, 157.6, 120.0))
    assertTrue(impl.isEffective(Planet.VENUS, 142.5, Planet.MARS, 30.0, 120.0))
    assertFalse(impl.isEffective(Planet.VENUS, 142.4, Planet.MARS, 30.0, 120.0))

    //火木容許度 8.5
    assertTrue(impl.isEffective(Planet.MARS, 0.0, Planet.JUPITER, 278.5, 90.0))
    assertFalse(impl.isEffective(Planet.MARS, 0.0, Planet.JUPITER, 278.6, 90.0))
    assertTrue(impl.isEffective(Planet.MARS, 261.5, Planet.JUPITER, 0.0, 90.0))
    assertFalse(impl.isEffective(Planet.MARS, 261.4, Planet.JUPITER, 0.0, 90.0))

    //木土容許度 9
    assertTrue(impl.isEffective(Planet.JUPITER, 0.0, Planet.SATURN, 129.0, 120.0))
    assertFalse(impl.isEffective(Planet.JUPITER, 0.0, Planet.SATURN, 130.0, 120.0))
    assertTrue(impl.isEffective(Planet.JUPITER, 231.0, Planet.SATURN, 0.0, 120.0))
    assertFalse(impl.isEffective(Planet.JUPITER, 230.0, Planet.SATURN, 0.0, 120.0))

    //土天容許度 7
    assertTrue(impl.isEffective(Planet.SATURN, 90.0, Planet.URANUS, 23.0, 60.0))
    assertFalse(impl.isEffective(Planet.SATURN, 90.0, Planet.URANUS, 22.0, 60.0))
    assertTrue(impl.isEffective(Planet.SATURN, 37.0, Planet.URANUS, 90.0, 60.0))
    assertFalse(impl.isEffective(Planet.SATURN, 38.0, Planet.URANUS, 90.0, 60.0))

    //天海容許度 5
    assertTrue(impl.isEffective(Planet.URANUS, 0.0, Planet.NEPTUNE, 95.0, 90.0))
    assertFalse(impl.isEffective(Planet.URANUS, 0.0, Planet.NEPTUNE, 96.0, 90.0))
    assertTrue(impl.isEffective(Planet.URANUS, 85.0, Planet.NEPTUNE, 0.0, 90.0))
    assertFalse(impl.isEffective(Planet.URANUS, 84.0, Planet.NEPTUNE, 0.0, 90.0))

    //海冥容許度 5
    assertTrue(impl.isEffective(Planet.NEPTUNE, 0.0, Planet.PLUTO, 95.0, 90.0))
    assertFalse(impl.isEffective(Planet.NEPTUNE, 0.0, Planet.PLUTO, 96.0, 90.0))
    assertTrue(impl.isEffective(Planet.NEPTUNE, 85.0, Planet.PLUTO, 0.0, 90.0))
    assertFalse(impl.isEffective(Planet.NEPTUNE, 84.0, Planet.PLUTO, 0.0, 90.0))
  }

  /** 多重角度比對，只要一個成立，就該傳回 true  */
  @Test
  fun testIsEffective_multipleAspects() {
    val impl = AspectEffectiveClassical()
    impl.setPlanetOrbsImpl(PointDiameterAlBiruniImpl())

    assertTrue(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, Aspect.SQUARE))
    assertTrue(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, Aspect.SQUARE, Aspect.TRINE, Aspect.OPPOSITION))
    assertTrue(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, Aspect.TRINE, Aspect.OPPOSITION, Aspect.SQUARE))
    assertFalse(impl.isEffective(Planet.SUN, 10.0, Planet.MOON, 113.5, Aspect.TRINE, Aspect.OPPOSITION, Aspect.CONJUNCTION))

    //月水容許度 9.5
    assertTrue(impl.isEffective(Planet.MOON, 10.0, Planet.MERCURY, 90.5, Aspect.SQUARE))
    assertFalse(impl.isEffective(Planet.MOON, 10.0, Planet.MERCURY, 90.4, Aspect.SQUARE, Aspect.TRINE))
    assertTrue(impl.isEffective(Planet.MOON, 90.5, Planet.MERCURY, 10.0, Aspect.OPPOSITION, Aspect.CONJUNCTION, Aspect.SQUARE))
    assertFalse(impl.isEffective(Planet.MOON, 90.4, Planet.MERCURY, 10.0, Aspect.OPPOSITION, Aspect.CONJUNCTION, Aspect.SQUARE))
  }
}
