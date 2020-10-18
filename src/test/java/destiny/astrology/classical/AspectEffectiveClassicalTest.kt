/**
 * @author smallufo
 * Created on 2007/11/22 at 上午 3:32:28
 */
package destiny.astrology.classical

import destiny.astrology.Aspect.*
import destiny.astrology.Planet.*
import mu.KotlinLogging
import kotlin.test.*

class AspectEffectiveClassicalTest {

  val logger = KotlinLogging.logger {}

  @Test
  fun testEquality() {
    val impl1 = AspectEffectiveClassical(PointDiameterAlBiruniImpl())
    val impl2 = AspectEffectiveClassical(PointDiameterAlBiruniImpl(), 0.6)
    assertEquals(impl1, impl2)

    val impl3 = AspectEffectiveClassical(PointDiameterAlBiruniImpl(), 0.7)
    assertNotEquals(impl1, impl3)
  }

  /** 測試注入 PlanetOrbsDefaultImpl 的實作  */
  @Test
  fun testIsEffective_PlanetOrbsDefaultImpl() {
    val impl = AspectEffectiveClassical(PointDiameterAlBiruniImpl())

    // 日月容許度 13.5
    assertTrue(impl.isEffective(SUN, 10.0, MOON, 113.5, 90.0))
    assertTrue(impl.isEffective(SUN, 10.0, MOON, 113.5, SQUARE))
    assertFalse(impl.isEffective(SUN, 10.0, MOON, 113.6, 90.0))
    assertFalse(impl.isEffective(SUN, 10.0, MOON, 113.6, SQUARE))
    assertTrue(impl.isEffective(SUN, 113.5, MOON, 10.0, 90.0))
    assertTrue(impl.isEffective(SUN, 113.5, MOON, 10.0, SQUARE))
    assertFalse(impl.isEffective(SUN, 113.6, MOON, 10.0, 90.0))
    assertFalse(impl.isEffective(SUN, 113.6, MOON, 10.0, SQUARE))

    assertTrue(impl.isEffective(SUN, 0.0, MOON, 193.5, OPPOSITION))
    assertFalse(impl.isEffective(SUN, 0.0, MOON, 193.6, OPPOSITION))

    impl.getEffectiveErrorAndScore(SUN, 0.0, MOON, 193.5, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(0.6, it.second)
    }
    impl.getEffectiveErrorAndScore(SUN, 0.0, MOON, 180.0, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(1.0, it.second)
    }

    assertTrue(impl.isEffective(SUN, 340.0, MOON, 113.5, 120.0))
    assertFalse(impl.isEffective(SUN, 340.0, MOON, 113.6, 120.0))
    assertTrue(impl.isEffective(SUN, 113.5, MOON, 340.0, 120.0))
    assertFalse(impl.isEffective(SUN, 113.6, MOON, 340.0, 120.0))

    //月水容許度 9.5
    assertTrue(impl.isEffective(MOON, 10.0, MERCURY, 90.5, 90.0))
    assertFalse(impl.isEffective(MOON, 10.0, MERCURY, 90.4, 90.0))
    assertTrue(impl.isEffective(MOON, 90.5, MERCURY, 10.0, 90.0))
    assertFalse(impl.isEffective(MOON, 90.4, MERCURY, 10.0, 90.0))

    //水金容許度 7
    assertTrue(impl.isEffective(MERCURY, 20.0, VENUS, 73.0, 60.0))
    assertFalse(impl.isEffective(MERCURY, 20.0, VENUS, 72.0, 60.0))
    assertTrue(impl.isEffective(MERCURY, 87.0, VENUS, 20.0, 60.0))
    assertFalse(impl.isEffective(MERCURY, 88.0, VENUS, 20.0, 60.0))

    //金火容許度 7.5
    assertTrue(impl.isEffective(VENUS, 30.0, MARS, 157.5, 120.0))
    assertFalse(impl.isEffective(VENUS, 30.0, MARS, 157.6, 120.0))
    assertTrue(impl.isEffective(VENUS, 142.5, MARS, 30.0, 120.0))
    assertFalse(impl.isEffective(VENUS, 142.4, MARS, 30.0, 120.0))

    //火木容許度 8.5
    assertTrue(impl.isEffective(MARS, 0.0, JUPITER, 278.5, 90.0))
    assertFalse(impl.isEffective(MARS, 0.0, JUPITER, 278.6, 90.0))
    assertTrue(impl.isEffective(MARS, 261.5, JUPITER, 0.0, 90.0))
    assertFalse(impl.isEffective(MARS, 261.4, JUPITER, 0.0, 90.0))

    //木土容許度 9
    assertTrue(impl.isEffective(JUPITER, 0.0, SATURN, 129.0, 120.0))
    assertFalse(impl.isEffective(JUPITER, 0.0, SATURN, 130.0, 120.0))
    assertTrue(impl.isEffective(JUPITER, 231.0, SATURN, 0.0, 120.0))
    assertFalse(impl.isEffective(JUPITER, 230.0, SATURN, 0.0, 120.0))

    //土天容許度 7
    assertTrue(impl.isEffective(SATURN, 90.0, URANUS, 23.0, 60.0))
    assertFalse(impl.isEffective(SATURN, 90.0, URANUS, 22.0, 60.0))
    assertTrue(impl.isEffective(SATURN, 37.0, URANUS, 90.0, 60.0))
    assertFalse(impl.isEffective(SATURN, 38.0, URANUS, 90.0, 60.0))

    //天海容許度 5
    assertTrue(impl.isEffective(URANUS, 0.0, NEPTUNE, 95.0, 90.0))
    assertFalse(impl.isEffective(URANUS, 0.0, NEPTUNE, 96.0, 90.0))
    assertTrue(impl.isEffective(URANUS, 85.0, NEPTUNE, 0.0, 90.0))
    assertFalse(impl.isEffective(URANUS, 84.0, NEPTUNE, 0.0, 90.0))

    //海冥容許度 5
    assertTrue(impl.isEffective(NEPTUNE, 0.0, PLUTO, 95.0, 90.0))
    assertFalse(impl.isEffective(NEPTUNE, 0.0, PLUTO, 96.0, 90.0))
    assertTrue(impl.isEffective(NEPTUNE, 85.0, PLUTO, 0.0, 90.0))
    assertFalse(impl.isEffective(NEPTUNE, 84.0, PLUTO, 0.0, 90.0))
  }

  /** 多重角度比對，只要一個成立，就該傳回 true  */
  @Test
  fun testIsEffective_multipleAspects() {
    val impl = AspectEffectiveClassical(PointDiameterAlBiruniImpl())

    assertTrue(impl.isEffective(SUN, 10.0, MOON, 113.5, SQUARE))
    assertTrue(impl.isEffective(SUN, 10.0, MOON, 113.5, SQUARE, TRINE, OPPOSITION))
    assertTrue(impl.isEffective(SUN, 10.0, MOON, 113.5, TRINE, OPPOSITION, SQUARE))
    assertFalse(impl.isEffective(SUN, 10.0, MOON, 113.5, TRINE, OPPOSITION, CONJUNCTION))

    //月水容許度 9.5
    assertTrue(impl.isEffective(MOON, 10.0, MERCURY, 90.5, SQUARE))
    assertFalse(impl.isEffective(MOON, 10.0, MERCURY, 90.4, SQUARE, TRINE))
    assertTrue(impl.isEffective(MOON, 90.5, MERCURY, 10.0, OPPOSITION, CONJUNCTION, SQUARE))
    assertFalse(impl.isEffective(MOON, 90.4, MERCURY, 10.0, OPPOSITION, CONJUNCTION, SQUARE))
  }


}
