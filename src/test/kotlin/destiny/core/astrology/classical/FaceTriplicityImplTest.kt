package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FaceTriplicityImplTest {

  private val impl = FaceTriplicityImpl

  @Test
  fun aries() {
    assertSame(MARS, impl.getPoint(0.0.toZodiacDegree()))
    assertSame(MARS, impl.getPoint(9.99.toZodiacDegree()))
    assertSame(SUN, impl.getPoint(10.0.toZodiacDegree()))
    assertSame(JUPITER, impl.getPoint(20.0.toZodiacDegree()))
    assertSame(JUPITER, impl.getPoint(29.999.toZodiacDegree()))
  }

  @Test
  fun taurus() {
    with(impl) {
      assertSame(VENUS, TAURUS.getFacePoint(5.0))
      assertSame(MERCURY, TAURUS.getFacePoint(15.0))
      assertSame(SATURN, TAURUS.getFacePoint(25.0))
    }
  }

  /** 天蠍：天蠍(火)、雙魚(木)、巨蟹(月) ；水瓶：水瓶(土)、雙子(水)、天秤(金) ；雙魚：雙魚(木)、巨蟹(月)、天蠍(火) */
  @Test
  fun fixedAndMutableWaterAir() {
    with(impl) {
      assertEquals(listOf(MARS, JUPITER, MOON), listOf(5.0, 15.0, 25.0).map { SCORPIO.getFacePoint(it) })
      assertEquals(listOf(SATURN, MERCURY, VENUS), listOf(5.0, 15.0, 25.0).map { AQUARIUS.getFacePoint(it) })
      assertEquals(listOf(JUPITER, MOON, MARS), listOf(5.0, 15.0, 25.0).map { PISCES.getFacePoint(it) })
      assertSame(MARS, PISCES.getFacePoint(29.9999))
    }
  }

  @Test
  fun firstDecanIsSignRuler() {
    entries.forEach { sign ->
      val ruler = with(RulerPtolemyImpl) { sign.getRulerPoint() }
      assertSame<AstroPoint?>(ruler, with(impl) { sign.getFacePoint(1.0) }, sign.name)
    }
  }

  @Test
  fun overlapWithChaldean() {
    val same = (0 until 36).filter { i ->
      val d = (i * 10 + 5.0).toZodiacDegree()
      impl.getPoint(d) === FaceChaldeanImpl.getPoint(d)
    }
    assertEquals(listOf(0, 1, 5, 13, 14, 19, 21, 31, 35), same)
  }
}
