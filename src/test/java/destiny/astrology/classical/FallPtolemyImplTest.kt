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

  private val fallImpl : IFall = FallPtolemyImpl()

  @Test
  fun getPoint() {
    assertSame(SATURN, fallImpl.getPoint(ARIES))
    assertNull(fallImpl.getPoint(TAURUS))
    assertSame(LunarNode.SOUTH_TRUE, fallImpl.getPoint(GEMINI))
    //assertSame(LunarNode.SOUTH_MEAN , fallImpl.getDetrimentPoint(GEMINI))
    assertSame(MARS, fallImpl.getPoint(CANCER))
    assertNull(fallImpl.getPoint(LEO))
    assertSame(VENUS, fallImpl.getPoint(VIRGO))
    assertSame(SUN, fallImpl.getPoint(LIBRA))
    assertSame(MOON, fallImpl.getPoint(SCORPIO))
    assertSame(LunarNode.NORTH_TRUE, fallImpl.getPoint(SAGITTARIUS))
    //assertSame(LunarNode.NORTH_MEAN, fallImpl.getDetrimentPoint(SAGITTARIUS))
    assertSame(JUPITER, fallImpl.getPoint(CAPRICORN))
    assertNull(fallImpl.getPoint(AQUARIUS))
    assertSame(MERCURY, fallImpl.getPoint(PISCES))
  }

  @Test
  fun getSign() {
    with(fallImpl) {
      assertSame(ARIES, SATURN.getFallingSign())
      assertSame(GEMINI, LunarNode.SOUTH_TRUE.getFallingSign())
      assertSame(GEMINI, LunarNode.SOUTH_MEAN.getFallingSign())
      assertSame(CANCER, MARS.getFallingSign())
      assertSame(VIRGO, VENUS.getFallingSign())
      assertSame(LIBRA, SUN.getFallingSign())
      assertSame(SCORPIO, MOON.getFallingSign())
      assertSame(SAGITTARIUS, LunarNode.NORTH_TRUE.getFallingSign())
      assertSame(SAGITTARIUS, LunarNode.NORTH_MEAN.getFallingSign())
      assertSame(CAPRICORN, JUPITER.getFallingSign())
      assertSame(PISCES, MERCURY.getFallingSign())
    }
  }
  
  /**
   * 測試星座的 FALL (-4) 星體及度數
   */
  @Test
  fun getPointDegree() {
    with(fallImpl) {
      assertEquals(PointDegree(SATURN, ARIES, 21.0), ARIES.getPointDegree())
      assertEquals(null, TAURUS.getPointDegree())
      assertEquals(PointDegree(MARS, CANCER, 28.0), CANCER.getPointDegree())
      assertEquals(null, LEO.getPointDegree())
      assertEquals(PointDegree(VENUS, VIRGO, 27.0), VIRGO.getPointDegree())
      assertEquals(PointDegree(SUN, LIBRA, 19.0), LIBRA.getPointDegree())
      assertEquals(PointDegree(MOON, SCORPIO, 3.0), SCORPIO.getPointDegree())
      assertEquals(PointDegree(JUPITER, CAPRICORN, 15.0), CAPRICORN.getPointDegree())
      assertEquals(null, AQUARIUS.getPointDegree())
      assertEquals(PointDegree(MERCURY, PISCES, 15.0), PISCES.getPointDegree())
      //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.GEMINI      ,  3), impl.getPointDegree(ZodiacSign.GEMINI     ));
      //assertEquals( new PointDegree(LunarNode.NORTH     , ZodiacSign.SAGITTARIUS ,  3), impl.getPointDegree(ZodiacSign.SAGITTARIUS));
    }

  }
}
