/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertSame

class TermPtolomyImplTest {

  val impl = TermPtolomyImpl()

  @Test
  fun getPoint_from_degree() {
    //戌
    assertSame(JUPITER, impl.getPoint(0.0))
    assertSame(JUPITER, impl.getPoint(5.99))
    assertSame(VENUS, impl.getPoint(6.0))
    assertSame(MERCURY, impl.getPoint(14.0))
    assertSame(MARS, impl.getPoint(21.0))
    assertSame(SATURN, impl.getPoint(26.0))
    //酉
    assertSame(VENUS, impl.getPoint(30.0))
    assertSame(VENUS, impl.getPoint(37.99))
    assertSame(MARS, impl.getPoint(56.0))
    //申
    assertSame(MERCURY, impl.getPoint(60.0))
    assertSame(VENUS, impl.getPoint(80.99))

    //亥
    assertSame(VENUS, impl.getPoint(330.0))
    assertSame(JUPITER, impl.getPoint(338.0))
    assertSame(MERCURY, impl.getPoint(344.0))
    assertSame(MARS, impl.getPoint(350.0))
    assertSame(MARS, impl.getPoint(355.99))
    assertSame(SATURN, impl.getPoint(356.0))
    assertSame(SATURN, impl.getPoint(359.99))
    //破 360度 , 回到 戌
    assertSame(JUPITER, impl.getPoint(360.0))
    assertSame(JUPITER, impl.getPoint(365.99))
    assertSame(SATURN, impl.getPoint(386.0))
    //酉
    assertSame(VENUS, impl.getPoint(390.0))
    //破720 , 回到 戌
    assertSame(JUPITER, impl.getPoint(720.0))
    assertSame(JUPITER, impl.getPoint(725.99))

    //度數小於0 , 往前推到亥
    assertSame(SATURN, impl.getPoint(-0.001))
    // -30 , 仍是亥
    assertSame(VENUS, impl.getPoint(-30.0))
    //-359 , 戌宮
    assertSame(JUPITER, impl.getPoint(-359.0))
  }

  @Test
  fun getPoint_from_sign_and_degree() {
    with(impl) {
      //戌
      assertSame(JUPITER, ARIES.getTermPoint(0.0))
      assertSame(JUPITER, ARIES.getTermPoint(5.99))
      assertSame(VENUS, ARIES.getTermPoint(6.0))
      assertSame(VENUS, ARIES.getTermPoint(13.99))
      assertSame(MERCURY, ARIES.getTermPoint(14.0))
      assertSame(MERCURY, ARIES.getTermPoint(20.99))
      assertSame(MARS, ARIES.getTermPoint(21.0))
      assertSame(MARS, ARIES.getTermPoint(25.99))
      assertSame(SATURN, ARIES.getTermPoint(26.0))
      assertSame(SATURN, ARIES.getTermPoint(29.99))

      //戌 , 破30度，應該進到酉
      assertSame(VENUS, ARIES.getTermPoint(30.0))
      assertSame(VENUS, ARIES.getTermPoint(37.99))
      //酉
      assertSame(VENUS, TAURUS.getTermPoint(0.0))
      assertSame(VENUS, TAURUS.getTermPoint(7.99))
      assertSame(MERCURY, TAURUS.getTermPoint(8.0))
      assertSame(MERCURY, TAURUS.getTermPoint(14.99))
      assertSame(JUPITER, TAURUS.getTermPoint(15.0))
      assertSame(JUPITER, TAURUS.getTermPoint(21.99))
      assertSame(SATURN, TAURUS.getTermPoint(22.0))
      assertSame(SATURN, TAURUS.getTermPoint(25.99))
      assertSame(MARS, TAURUS.getTermPoint(26.0))
      assertSame(MARS, TAURUS.getTermPoint(29.99))
      //酉 , 破 30 度 , 應該進到申
      assertSame(MERCURY, TAURUS.getTermPoint(30.0))
      assertSame(MERCURY, TAURUS.getTermPoint(36.99))

      //亥
      assertSame(VENUS, PISCES.getTermPoint(0.0))
      assertSame(VENUS, PISCES.getTermPoint(7.99))
      assertSame(JUPITER, PISCES.getTermPoint(8.0))
      assertSame(JUPITER, PISCES.getTermPoint(13.99))
      assertSame(MERCURY, PISCES.getTermPoint(14.0))
      assertSame(MERCURY, PISCES.getTermPoint(19.99))
      assertSame(MARS, PISCES.getTermPoint(20.0))
      assertSame(MARS, PISCES.getTermPoint(25.99))
      assertSame(SATURN, PISCES.getTermPoint(26.0))
      assertSame(SATURN, PISCES.getTermPoint(29.99))
      //破30 , 回到戌
      assertSame(JUPITER, PISCES.getTermPoint(30.0))
      assertSame(JUPITER, PISCES.getTermPoint(35.99))
      assertSame(VENUS, PISCES.getTermPoint(36.0))
    }


  }
}
