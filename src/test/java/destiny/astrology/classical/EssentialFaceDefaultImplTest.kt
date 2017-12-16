/**
 * @author smallufo
 * Created on 2007/11/28 at 下午 9:20:46
 */
package destiny.astrology.classical

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import kotlin.test.Test
import kotlin.test.assertSame

class EssentialFaceDefaultImplTest {
  @Test
  fun testGetStarDouble() {
    val impl = EssentialFaceDefaultImpl()

    assertSame(impl.getFaceStar(0.0) , Planet.MARS)
    assertSame(impl.getFaceStar(9.99) , Planet.MARS)
    assertSame(impl.getFaceStar(10.0) , Planet.SUN)
    assertSame(impl.getFaceStar(19.99) , Planet.SUN)
    assertSame(impl.getFaceStar(20.0) , Planet.VENUS)
    assertSame(impl.getFaceStar(20.99) , Planet.VENUS)

    assertSame(impl.getFaceStar(30.0) , Planet.MERCURY)
    assertSame(impl.getFaceStar(40.0) , Planet.MOON)
    assertSame(impl.getFaceStar(50.0) , Planet.SATURN)

    assertSame(impl.getFaceStar(60.0) , Planet.JUPITER)
    assertSame(impl.getFaceStar(70.0) , Planet.MARS)
    assertSame(impl.getFaceStar(80.0) , Planet.SUN)

    assertSame(impl.getFaceStar(90.0) , Planet.VENUS)
    assertSame(impl.getFaceStar(100.0) , Planet.MERCURY)
    assertSame(impl.getFaceStar(110.0) , Planet.MOON)

    assertSame(impl.getFaceStar(120.0) , Planet.SATURN)
    assertSame(impl.getFaceStar(130.0) , Planet.JUPITER)
    assertSame(impl.getFaceStar(140.0) , Planet.MARS)

    assertSame(impl.getFaceStar(150.0) , Planet.SUN)
    assertSame(impl.getFaceStar(160.0) , Planet.VENUS)
    assertSame(impl.getFaceStar(170.0) , Planet.MERCURY)

    assertSame(impl.getFaceStar(180.0) , Planet.MOON)
    assertSame(impl.getFaceStar(190.0) , Planet.SATURN)
    assertSame(impl.getFaceStar(200.0) , Planet.JUPITER)

    assertSame(impl.getFaceStar(210.0) , Planet.MARS)
    assertSame(impl.getFaceStar(220.0) , Planet.SUN)
    assertSame(impl.getFaceStar(230.0) , Planet.VENUS)

    assertSame(impl.getFaceStar(240.0) , Planet.MERCURY)
    assertSame(impl.getFaceStar(250.0) , Planet.MOON)
    assertSame(impl.getFaceStar(260.0) , Planet.SATURN)

    assertSame(impl.getFaceStar(270.0) , Planet.JUPITER)
    assertSame(impl.getFaceStar(280.0) , Planet.MARS)
    assertSame(impl.getFaceStar(290.0) , Planet.SUN)

    assertSame(impl.getFaceStar(300.0) , Planet.VENUS)
    assertSame(impl.getFaceStar(310.0) , Planet.MERCURY)
    assertSame(impl.getFaceStar(320.0) , Planet.MOON)

    assertSame(impl.getFaceStar(330.0) , Planet.SATURN)
    assertSame(impl.getFaceStar(340.0) , Planet.JUPITER)
    assertSame(impl.getFaceStar(350.0) , Planet.MARS)
    assertSame(impl.getFaceStar(359.99) , Planet.MARS)
    //破360
    assertSame(impl.getFaceStar(360.0) , Planet.MARS)
    assertSame(impl.getFaceStar(361.0) , Planet.MARS)
    assertSame(impl.getFaceStar(369.99) , Planet.MARS)
    assertSame(impl.getFaceStar(720.0) , Planet.MARS)
    assertSame(impl.getFaceStar(721.0) , Planet.MARS)
    assertSame(impl.getFaceStar(729.99) , Planet.MARS)

  }

  @Test
  fun testGetStarZodiacSignDouble() {
    val impl = EssentialFaceDefaultImpl()

    //戌
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 0.0) , Planet.MARS)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 9.99) , Planet.MARS)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 10.0) , Planet.SUN)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 19.99) , Planet.SUN)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 20.0) , Planet.VENUS)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 29.99) , Planet.VENUS)
    //破30
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 30.0) , Planet.MERCURY)
    assertSame(impl.getFaceStar(ZodiacSign.ARIES, 39.99) , Planet.MERCURY)

    //亥
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 0.0) , Planet.SATURN)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 9.99) , Planet.SATURN)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 10.0) , Planet.JUPITER)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 19.99) , Planet.JUPITER)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 20.0) , Planet.MARS)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 29.99) , Planet.MARS)
    //破30 , 回到戌
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 30.0) , Planet.MARS)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 39.990) , Planet.MARS)
    assertSame(impl.getFaceStar(ZodiacSign.PISCES, 40.0) , Planet.SUN)
  }

}
