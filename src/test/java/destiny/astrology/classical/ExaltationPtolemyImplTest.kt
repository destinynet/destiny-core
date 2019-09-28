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

class ExaltationPtolemyImplTest {

  private val exaltImpl: IExaltation = ExaltationPtolemyImpl()

  @Test
  fun `從星座找尋 EXALT 的星體`() {
    assertSame(SUN, exaltImpl.getPoint(ARIES))
    assertSame(MOON, exaltImpl.getPoint(TAURUS))
    assertSame(LunarNode.NORTH_TRUE, exaltImpl.getPoint(GEMINI))
//    assertSame(LunarNode.NORTH_MEAN, exaltImpl.getDetrimentPoint(ZodiacSign.GEMINI))
    assertSame(JUPITER, exaltImpl.getPoint(CANCER))
    assertNull(exaltImpl.getPoint(LEO))
    assertSame(MERCURY, exaltImpl.getPoint(VIRGO))
    assertSame(SATURN, exaltImpl.getPoint(LIBRA))
    assertNull(exaltImpl.getPoint(SCORPIO))

    assertSame(LunarNode.SOUTH_TRUE, exaltImpl.getPoint(SAGITTARIUS))
//    assertSame(LunarNode.SOUTH_MEAN, exaltImpl.getDetrimentPoint(SAGITTARIUS))
    assertSame(MARS, exaltImpl.getPoint(CAPRICORN))
    assertNull(exaltImpl.getPoint(AQUARIUS))
    assertSame(VENUS, exaltImpl.getPoint(PISCES))
  }

  @Test
  fun `從星體詢問在哪星座 EXALT`() {
    with(exaltImpl) {
      assertSame(ARIES, SUN.getExaltSign())
      assertSame(TAURUS, MOON.getExaltSign())
      assertSame(GEMINI, LunarNode.NORTH_TRUE.getExaltSign())
      assertSame(GEMINI, LunarNode.NORTH_MEAN.getExaltSign())
      assertSame(CANCER, JUPITER.getExaltSign())
      assertSame(VIRGO, MERCURY.getExaltSign())
      assertSame(LIBRA, SATURN.getExaltSign())
      assertSame(SAGITTARIUS, LunarNode.SOUTH_TRUE.getExaltSign())
      assertSame(SAGITTARIUS, LunarNode.SOUTH_MEAN.getExaltSign())
      assertSame(CAPRICORN, MARS.getExaltSign())
      assertSame(PISCES, VENUS.getExaltSign())
    }
  }


  /**
   * 測試星座的 EXALT (+4) 星體及度數
   */
  @Test
  fun getPointDegree() {
    with(exaltImpl) {
      assertEquals(PointDegree(SUN, ARIES, 19.0), ARIES.getExaltPointDegree())
      assertEquals(PointDegree(MOON, TAURUS, 3.0), TAURUS.getExaltPointDegree())
      //assertEquals( new PointDegree(LunarNode.NORTH , ZodiacSign.GEMINI      ,  3), impl.getPointDegree(ZodiacSign.GEMINI     ));
      assertEquals(PointDegree(JUPITER, CANCER, 15.0), CANCER.getExaltPointDegree())
      assertEquals(null, LEO.getExaltPointDegree())
      assertEquals(PointDegree(MERCURY, VIRGO, 15.0), VIRGO.getExaltPointDegree())
      assertEquals(PointDegree(SATURN, LIBRA, 21.0), LIBRA.getExaltPointDegree())
      assertEquals(null, SCORPIO.getExaltPointDegree())
      //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.SAGITTARIUS ,  3), impl.getPointDegree(ZodiacSign.SAGITTARIUS));
      assertEquals(PointDegree(MARS, CAPRICORN, 28.0), CAPRICORN.getExaltPointDegree())
      assertEquals(null, AQUARIUS.getExaltPointDegree())
      assertEquals(PointDegree(VENUS, PISCES, 27.0), PISCES.getExaltPointDegree())
    }

  }
}
