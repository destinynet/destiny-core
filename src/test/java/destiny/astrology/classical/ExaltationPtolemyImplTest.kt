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

  private val exaltImpl: ExaltationPtolemyImpl = ExaltationPtolemyImpl()

  @Test
  fun `從星座找尋 EXALT 的星體`() {
    assertSame(SUN, exaltImpl.getPoint(ARIES))
    assertSame(MOON, exaltImpl.getPoint(TAURUS))
    assertSame(LunarNode.NORTH_TRUE, exaltImpl.getPoint(GEMINI))
//    assertSame(LunarNode.NORTH_MEAN, exaltImpl.getPoint(ZodiacSign.GEMINI))
    assertSame(JUPITER, exaltImpl.getPoint(CANCER))
    assertNull(exaltImpl.getPoint(LEO))
    assertSame(MERCURY, exaltImpl.getPoint(VIRGO))
    assertSame(SATURN, exaltImpl.getPoint(LIBRA))
    assertNull(exaltImpl.getPoint(SCORPIO))

    assertSame(LunarNode.SOUTH_TRUE, exaltImpl.getPoint(SAGITTARIUS))
//    assertSame(LunarNode.SOUTH_MEAN, exaltImpl.getPoint(SAGITTARIUS))
    assertSame(MARS, exaltImpl.getPoint(CAPRICORN))
    assertNull(exaltImpl.getPoint(AQUARIUS))
    assertSame(VENUS, exaltImpl.getPoint(PISCES))
  }

  @Test
  fun `從星體詢問在哪星座 EXALT`() {
    assertSame(ARIES, exaltImpl.getSign(SUN))
    assertSame(TAURUS, exaltImpl.getSign(MOON))
    assertSame(GEMINI, exaltImpl.getSign(LunarNode.NORTH_TRUE))
    assertSame(GEMINI, exaltImpl.getSign(LunarNode.NORTH_MEAN))
    assertSame(CANCER, exaltImpl.getSign(JUPITER))
    assertSame(VIRGO, exaltImpl.getSign(MERCURY))
    assertSame(LIBRA, exaltImpl.getSign(SATURN))
    assertSame(SAGITTARIUS, exaltImpl.getSign(LunarNode.SOUTH_TRUE))
    assertSame(SAGITTARIUS, exaltImpl.getSign(LunarNode.SOUTH_MEAN))
    assertSame(CAPRICORN , exaltImpl.getSign(MARS))
    assertSame(PISCES , exaltImpl.getSign(VENUS))
  }
  
  
  /**
   * 測試星座的 EXALT (+4) 星體及度數
   */
  @Test
  fun getPointDegree() {
    assertEquals(PointDegree(SUN, ARIES, 19.0), exaltImpl.getPointDegree(ARIES))
    assertEquals(PointDegree(MOON, TAURUS, 3.0), exaltImpl.getPointDegree(TAURUS))
    //assertEquals( new PointDegree(LunarNode.NORTH , ZodiacSign.GEMINI      ,  3), impl.getPointDegree(ZodiacSign.GEMINI     ));
    assertEquals(PointDegree(JUPITER, CANCER, 15.0), exaltImpl.getPointDegree(CANCER))
    assertEquals(null, exaltImpl.getPointDegree(LEO))
    assertEquals(PointDegree(MERCURY, VIRGO, 15.0), exaltImpl.getPointDegree(VIRGO))
    assertEquals(PointDegree(SATURN, LIBRA, 21.0), exaltImpl.getPointDegree(LIBRA))
    assertEquals(null, exaltImpl.getPointDegree(SCORPIO))
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.SAGITTARIUS ,  3), impl.getPointDegree(ZodiacSign.SAGITTARIUS));
    assertEquals(PointDegree(MARS, CAPRICORN, 28.0), exaltImpl.getPointDegree(CAPRICORN))
    assertEquals(null, exaltImpl.getPointDegree(AQUARIUS))
    assertEquals(PointDegree(VENUS, PISCES, 27.0), exaltImpl.getPointDegree(PISCES))
  }
}