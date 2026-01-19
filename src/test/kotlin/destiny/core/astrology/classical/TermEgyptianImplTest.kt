/**
 * Created by smallufo on 2026-01-19.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TermEgyptianImplTest {

  private val impl = TermEgyptianImpl

  @Test
  fun getPoint_from_degree() {
    // Aries: Jupiter 0-6, Venus 6-12, Mercury 12-20, Mars 20-25, Saturn 25-30
    assertSame(JUPITER, impl.getPoint(0.0.toZodiacDegree()))
    assertSame(JUPITER, impl.getPoint(5.99.toZodiacDegree()))
    assertSame(VENUS, impl.getPoint(6.0.toZodiacDegree()))
    assertSame(VENUS, impl.getPoint(11.99.toZodiacDegree()))
    assertSame(MERCURY, impl.getPoint(12.0.toZodiacDegree()))
    assertSame(MERCURY, impl.getPoint(19.99.toZodiacDegree()))
    assertSame(MARS, impl.getPoint(20.0.toZodiacDegree()))
    assertSame(MARS, impl.getPoint(24.99.toZodiacDegree()))
    assertSame(SATURN, impl.getPoint(25.0.toZodiacDegree()))
    assertSame(SATURN, impl.getPoint(29.99.toZodiacDegree()))

    // Taurus: Venus 0-8, Mercury 8-14, Jupiter 14-22, Saturn 22-27, Mars 27-30
    assertSame(VENUS, impl.getPoint(30.0.toZodiacDegree()))
    assertSame(VENUS, impl.getPoint(37.99.toZodiacDegree()))
    assertSame(MERCURY, impl.getPoint(38.0.toZodiacDegree()))
    assertSame(JUPITER, impl.getPoint(44.0.toZodiacDegree()))
    assertSame(SATURN, impl.getPoint(52.0.toZodiacDegree()))
    assertSame(MARS, impl.getPoint(57.0.toZodiacDegree()))

    // Pisces: Venus 0-12, Jupiter 12-16, Mercury 16-19, Mars 19-28, Saturn 28-30
    assertSame(VENUS, impl.getPoint(330.0.toZodiacDegree()))
    assertSame(VENUS, impl.getPoint(341.99.toZodiacDegree()))
    assertSame(JUPITER, impl.getPoint(342.0.toZodiacDegree()))
    assertSame(MERCURY, impl.getPoint(346.0.toZodiacDegree()))
    assertSame(MARS, impl.getPoint(349.0.toZodiacDegree()))
    assertSame(SATURN, impl.getPoint(358.0.toZodiacDegree()))
  }

  @Test
  fun getPoint_from_sign_and_degree() {
    with(impl) {
      // Aries
      assertSame(JUPITER, ARIES.getTermPoint(0.0))
      assertSame(JUPITER, ARIES.getTermPoint(5.99))
      assertSame(VENUS, ARIES.getTermPoint(6.0))
      assertSame(MERCURY, ARIES.getTermPoint(12.0))
      assertSame(MARS, ARIES.getTermPoint(20.0))
      assertSame(SATURN, ARIES.getTermPoint(25.0))

      // Cancer: Mars 0-7, Venus 7-13, Mercury 13-19, Jupiter 19-26, Saturn 26-30
      assertSame(MARS, CANCER.getTermPoint(0.0))
      assertSame(MARS, CANCER.getTermPoint(6.99))
      assertSame(VENUS, CANCER.getTermPoint(7.0))
      assertSame(MERCURY, CANCER.getTermPoint(13.0))
      assertSame(JUPITER, CANCER.getTermPoint(19.0))
      assertSame(SATURN, CANCER.getTermPoint(26.0))
    }
  }

  @Test
  fun getTermBound() {
    // Aries first term: Jupiter 0-6
    impl.getTermBound(0.0.toZodiacDegree()).let { bound ->
      assertSame(JUPITER, bound.ruler)
      assertEquals(0.0, bound.fromDegree.value)
      assertEquals(6.0, bound.toDegree.value)
    }

    // Aries second term: Venus 6-12
    impl.getTermBound(6.0.toZodiacDegree()).let { bound ->
      assertSame(VENUS, bound.ruler)
      assertEquals(6.0, bound.fromDegree.value)
      assertEquals(12.0, bound.toDegree.value)
    }

    // Cancer first term: Mars 0-7 (at 90 degrees)
    impl.getTermBound(90.0.toZodiacDegree()).let { bound ->
      assertSame(MARS, bound.ruler)
      assertEquals(90.0, bound.fromDegree.value)
      assertEquals(97.0, bound.toDegree.value)
    }
  }

  @Test
  fun egyptianVsPtolemy_aries_differences() {
    // Compare Egyptian vs Ptolemy terms for Aries
    // Egyptian: Jupiter 6, Venus 12, Mercury 20, Mars 25, Saturn 30
    // Ptolemy:  Jupiter 6, Venus 14, Mercury 21, Mars 26, Saturn 30

    // At degree 7: both should have Venus
    assertSame(VENUS, TermEgyptianImpl.getPoint(7.0.toZodiacDegree()))
    assertSame(VENUS, TermPtolomyImpl.getPoint(7.0.toZodiacDegree()))

    // At degree 13: Egyptian has Mercury, Ptolemy has Venus
    assertSame(MERCURY, TermEgyptianImpl.getPoint(13.0.toZodiacDegree()))
    assertSame(VENUS, TermPtolomyImpl.getPoint(13.0.toZodiacDegree()))
  }
}
