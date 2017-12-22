/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.ARIES
import destiny.astrology.ZodiacSign.PISCES
import kotlin.test.Test
import kotlin.test.assertSame

class FacePtolomyImplTest {
  
  val impl = FacePtolomyImpl()

  @Test
  fun getPointFromDegree() {
    assertSame(impl.getPoint(0.0) , MARS)
    assertSame(impl.getPoint(9.99) , MARS)
    assertSame(impl.getPoint(10.0) , SUN)
    assertSame(impl.getPoint(19.99) , SUN)
    assertSame(impl.getPoint(20.0) , VENUS)
    assertSame(impl.getPoint(20.99) , VENUS)

    assertSame(impl.getPoint(30.0) , MERCURY)
    assertSame(impl.getPoint(40.0) , MOON)
    assertSame(impl.getPoint(50.0) , SATURN)

    assertSame(impl.getPoint(60.0) , JUPITER)
    assertSame(impl.getPoint(70.0) , MARS)
    assertSame(impl.getPoint(80.0) , SUN)

    assertSame(impl.getPoint(90.0) , VENUS)
    assertSame(impl.getPoint(100.0) , MERCURY)
    assertSame(impl.getPoint(110.0) , MOON)

    assertSame(impl.getPoint(120.0) , SATURN)
    assertSame(impl.getPoint(130.0) , JUPITER)
    assertSame(impl.getPoint(140.0) , MARS)

    assertSame(impl.getPoint(150.0) , SUN)
    assertSame(impl.getPoint(160.0) , VENUS)
    assertSame(impl.getPoint(170.0) , MERCURY)

    assertSame(impl.getPoint(180.0) , MOON)
    assertSame(impl.getPoint(190.0) , SATURN)
    assertSame(impl.getPoint(200.0) , JUPITER)

    assertSame(impl.getPoint(210.0) , MARS)
    assertSame(impl.getPoint(220.0) , SUN)
    assertSame(impl.getPoint(230.0) , VENUS)

    assertSame(impl.getPoint(240.0) , MERCURY)
    assertSame(impl.getPoint(250.0) , MOON)
    assertSame(impl.getPoint(260.0) , SATURN)

    assertSame(impl.getPoint(270.0) , JUPITER)
    assertSame(impl.getPoint(280.0) , MARS)
    assertSame(impl.getPoint(290.0) , SUN)

    assertSame(impl.getPoint(300.0) , VENUS)
    assertSame(impl.getPoint(310.0) , MERCURY)
    assertSame(impl.getPoint(320.0) , MOON)

    assertSame(impl.getPoint(330.0) , SATURN)
    assertSame(impl.getPoint(340.0) , JUPITER)
    assertSame(impl.getPoint(350.0) , MARS)
    assertSame(impl.getPoint(359.99) , MARS)
    //破360
    assertSame(impl.getPoint(360.0) , MARS)
    assertSame(impl.getPoint(361.0) , MARS)
    assertSame(impl.getPoint(369.99) , MARS)
    assertSame(impl.getPoint(720.0) , MARS)
    assertSame(impl.getPoint(721.0) , MARS)
    assertSame(impl.getPoint(729.99) , MARS)
  }
 
  @Test
  fun getPointFromSignDegree() {
    //戌
    assertSame(impl.getPoint(ARIES, 0.0) , MARS)
    assertSame(impl.getPoint(ARIES, 9.99) , MARS)
    assertSame(impl.getPoint(ARIES, 10.0) , SUN)
    assertSame(impl.getPoint(ARIES, 19.99) , SUN)
    assertSame(impl.getPoint(ARIES, 20.0) , VENUS)
    assertSame(impl.getPoint(ARIES, 29.99) , VENUS)
    //破30
    assertSame(impl.getPoint(ARIES, 30.0) , MERCURY)
    assertSame(impl.getPoint(ARIES, 39.99) , MERCURY)

    //亥
    assertSame(impl.getPoint(PISCES, 0.0) , SATURN)
    assertSame(impl.getPoint(PISCES, 9.99) , SATURN)
    assertSame(impl.getPoint(PISCES, 10.0) , JUPITER)
    assertSame(impl.getPoint(PISCES, 19.99) , JUPITER)
    assertSame(impl.getPoint(PISCES, 20.0) , MARS)
    assertSame(impl.getPoint(PISCES, 29.99) , MARS)
    //破30 , 回到戌
    assertSame(impl.getPoint(PISCES, 30.0) , MARS)
    assertSame(impl.getPoint(PISCES, 39.990) , MARS)
    assertSame(impl.getPoint(PISCES, 40.0) , SUN)
  }
  
}