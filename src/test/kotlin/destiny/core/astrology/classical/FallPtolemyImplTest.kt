/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet.*
import destiny.core.astrology.PointDegree
import destiny.core.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class FallPtolemyImplTest {

  private val fallImpl : IFall = FallPtolemyImpl

  @Test
  fun getPoint() {
    with(fallImpl) {
      assertSame(SATURN, ARIES.getFallPoint())
      assertNull(TAURUS.getFallPoint())
      assertSame(LunarNode.SOUTH_TRUE, GEMINI.getFallPoint())
      assertSame(MARS, CANCER.getFallPoint())
      assertNull(LEO.getFallPoint())
      assertSame(VENUS, VIRGO.getFallPoint())
      assertSame(SUN, LIBRA.getFallPoint())
      assertSame(MOON, SCORPIO.getFallPoint())
      assertSame(LunarNode.NORTH_TRUE, SAGITTARIUS.getFallPoint())
      assertSame(JUPITER, CAPRICORN.getFallPoint())
      assertNull(AQUARIUS.getFallPoint())
      assertSame(MERCURY, PISCES.getFallPoint())
      //assertSame(LunarNode.SOUTH_MEAN , fallImpl.getDetrimentPoint(GEMINI))
      //assertSame(LunarNode.NORTH_MEAN, fallImpl.getDetrimentPoint(SAGITTARIUS))
    }
  }

  @Test
  fun getSign() {
    with(fallImpl) {
      assertSame(ARIES, SATURN.getFallingSign())
      assertSame(GEMINI, LunarNode.SOUTH_TRUE.getFallingSign())
      assertSame(GEMINI, LunarNode.SOUTH_TRUE.getFallingSign())
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
