/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.LunarNode
import destiny.astrology.Planet.*
import destiny.astrology.PointDegree
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class FallPtolemyImplTest {

  private val fallImpl = FallPtolemyImpl()

  @Test
  fun getPoint() {
    assertSame(SATURN, fallImpl.getPoint(ARIES))
    assertNull(fallImpl.getPoint(TAURUS))
    assertSame(LunarNode.SOUTH_TRUE, fallImpl.getPoint(GEMINI))
    //assertSame(LunarNode.SOUTH_MEAN , fallImpl.getPoint(GEMINI))
    assertSame(MARS, fallImpl.getPoint(CANCER))
    assertNull(fallImpl.getPoint(LEO))
    assertSame(VENUS, fallImpl.getPoint(VIRGO))
    assertSame(SUN, fallImpl.getPoint(LIBRA))
    assertSame(MOON, fallImpl.getPoint(SCORPIO))
    assertSame(LunarNode.NORTH_TRUE, fallImpl.getPoint(SAGITTARIUS))
    //assertSame(LunarNode.NORTH_MEAN, fallImpl.getPoint(SAGITTARIUS))
    assertSame(JUPITER, fallImpl.getPoint(CAPRICORN))
    assertNull(fallImpl.getPoint(AQUARIUS))
    assertSame(MERCURY, fallImpl.getPoint(PISCES))
  }

  @Test
  fun getSign() {
    assertSame(ARIES, fallImpl.getSign(SATURN))
    assertSame(GEMINI, fallImpl.getSign(LunarNode.SOUTH_TRUE))
    assertSame(GEMINI, fallImpl.getSign(LunarNode.SOUTH_MEAN))
    assertSame(CANCER, fallImpl.getSign(MARS))
    assertSame(VIRGO, fallImpl.getSign(VENUS))
    assertSame(LIBRA, fallImpl.getSign(SUN))
    assertSame(SCORPIO, fallImpl.getSign(MOON))
    assertSame(SAGITTARIUS, fallImpl.getSign(LunarNode.NORTH_TRUE))
    assertSame(SAGITTARIUS, fallImpl.getSign(LunarNode.NORTH_MEAN))
    assertSame(CAPRICORN, fallImpl.getSign(JUPITER))
    assertSame(PISCES, fallImpl.getSign(MERCURY))
  }
  
  /**
   * 測試星座的 FALL (-4) 星體及度數
   */
  @Test
  fun getPointDegree() {
    assertEquals(PointDegree(SATURN, ARIES, 21.0), fallImpl.getPointDegree(ARIES))
    assertEquals(null, fallImpl.getPointDegree(TAURUS))
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.GEMINI      ,  3), impl.getPointDegree(ZodiacSign.GEMINI     ));
    assertEquals(PointDegree(MARS, CANCER, 28.0), fallImpl.getPointDegree(CANCER))
    assertEquals(null, fallImpl.getPointDegree(LEO))
    assertEquals(PointDegree(VENUS, VIRGO, 27.0), fallImpl.getPointDegree(VIRGO))
    assertEquals(PointDegree(SUN, LIBRA, 19.0), fallImpl.getPointDegree(LIBRA))
    assertEquals(PointDegree(MOON, SCORPIO, 3.0), fallImpl.getPointDegree(SCORPIO))
    //assertEquals( new PointDegree(LunarNode.NORTH     , ZodiacSign.SAGITTARIUS ,  3), impl.getPointDegree(ZodiacSign.SAGITTARIUS));
    assertEquals(PointDegree(JUPITER, CAPRICORN, 15.0), fallImpl.getPointDegree(CAPRICORN))
    assertEquals(null, fallImpl.getPointDegree(AQUARIUS))
    assertEquals(PointDegree(MERCURY, PISCES, 15.0), fallImpl.getPointDegree(PISCES))
  }
}