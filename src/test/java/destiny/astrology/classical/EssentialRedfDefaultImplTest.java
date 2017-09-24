/**
 * @author smallufo 
 * Created on 2007/11/26 at 下午 10:57:16
 */ 
package destiny.astrology.classical;

import destiny.astrology.Planet;
import destiny.astrology.PointDegree;
import destiny.astrology.ZodiacSign;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class EssentialRedfDefaultImplTest {

  EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();

  /** 測試 Ruler (旺) */
  @Test
  public void testRuler() {
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.ARIES       , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.TAURUS      , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.GEMINI      , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.MOON    , impl.getPoint(ZodiacSign.CANCER      , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.SUN     , impl.getPoint(ZodiacSign.LEO         , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.VIRGO       , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.LIBRA       , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.RULER).orElseThrow(RuntimeException::new));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.PISCES      , Dignity.RULER).orElseThrow(RuntimeException::new));
  }
  
  /** 測試 Detriment (陷) , 其值為對沖星座之Ruler */
  @Test
  public void testDetriment() {
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.ARIES       , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.TAURUS      , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.CANCER      , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.LEO         , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.VIRGO       , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.LIBRA       , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.MOON      , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.SUN       , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.PISCES      , Dignity.DETRIMENT).orElseThrow(RuntimeException::new));
  }

  /** 測試 Exaltation (廟) */
  @Test
  public void testExaltation() {
    assertSame(Planet.SUN     , impl.getPoint(ZodiacSign.ARIES       , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    assertSame(Planet.MOON    , impl.getPoint(ZodiacSign.TAURUS      , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    //assertSame(LunarNode.NORTH , impl.getPoint(ZodiacSign.GEMINI      , Dignity.EXALTATION));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.CANCER      , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    assertSame(null           , impl.getPoint(ZodiacSign.LEO         , Dignity.EXALTATION).orElse(null));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.VIRGO       , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.LIBRA       , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    assertSame(null           , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.EXALTATION).orElse(null));
    //assertSame(LunarNode.SOUTH , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.EXALTATION));
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
    assertSame(null           , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.EXALTATION).orElse(null));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.PISCES      , Dignity.EXALTATION).orElseThrow(RuntimeException::new));
  }
  
  /** 測試 Fall (落) , 其值為對沖星座之Exaltation之星體 */
  @Test
  public void testFall() {
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.ARIES       , Dignity.FALL).orElseThrow(RuntimeException::new));
    assertSame(null             , impl.getPoint(ZodiacSign.TAURUS      , Dignity.FALL).orElse(null));
    //assertSame(LunarNode.SOUTH   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.FALL));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.CANCER      , Dignity.FALL).orElseThrow(RuntimeException::new));
    assertSame(null             , impl.getPoint(ZodiacSign.LEO         , Dignity.FALL).orElse(null));
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.VIRGO       , Dignity.FALL).orElseThrow(RuntimeException::new));
    assertSame(Planet.SUN       , impl.getPoint(ZodiacSign.LIBRA       , Dignity.FALL).orElseThrow(RuntimeException::new));
    assertSame(Planet.MOON      , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.FALL).orElseThrow(RuntimeException::new));
    //assertSame(LunarNode.NORTH   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.FALL));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.FALL).orElseThrow(RuntimeException::new));
    assertSame(null             , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.FALL).orElse(null));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.PISCES      , Dignity.FALL).orElseThrow(RuntimeException::new));
  }

  
  /**
   * 測試星座的 Exaltation 星體及度數
   */
  @Test
  public void testGetExaltationStarDegree() {
    assertEquals( new PointDegree(Planet.SUN     , ZodiacSign.ARIES       , 19), impl.getExaltationStarDegree(ZodiacSign.ARIES      ).get());
    assertEquals( new PointDegree(Planet.MOON    , ZodiacSign.TAURUS      ,  3), impl.getExaltationStarDegree(ZodiacSign.TAURUS     ).get());
    //assertEquals( new PointDegree(LunarNode.NORTH , ZodiacSign.GEMINI      ,  3), impl.getExaltationStarDegree(ZodiacSign.GEMINI     ));
    assertEquals( new PointDegree(Planet.JUPITER , ZodiacSign.CANCER      , 15), impl.getExaltationStarDegree(ZodiacSign.CANCER     ).get());
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.LEO        ).orElse(null));
    assertEquals( new PointDegree(Planet.MERCURY , ZodiacSign.VIRGO       , 15), impl.getExaltationStarDegree(ZodiacSign.VIRGO      ).get());
    assertEquals( new PointDegree(Planet.SATURN  , ZodiacSign.LIBRA       , 21), impl.getExaltationStarDegree(ZodiacSign.LIBRA      ).get());
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.SCORPIO    ).orElse(null));
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.SAGITTARIUS ,  3), impl.getExaltationStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals( new PointDegree(Planet.MARS    , ZodiacSign.CAPRICORN   , 28), impl.getExaltationStarDegree(ZodiacSign.CAPRICORN  ).get());
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.AQUARIUS   ).orElse(null));
    assertEquals( new PointDegree(Planet.VENUS   , ZodiacSign.PISCES      , 27), impl.getExaltationStarDegree(ZodiacSign.PISCES     ).get());
  }

  @Test
  public void testGetFallStarDegree() {
    assertEquals( new PointDegree(Planet.SATURN  , ZodiacSign.ARIES       , 21), impl.getFallStarDegree(ZodiacSign.ARIES      ).get());
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.TAURUS     ).orElse(null));
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.GEMINI      ,  3), impl.getFallStarDegree(ZodiacSign.GEMINI     ));
    assertEquals( new PointDegree(Planet.MARS    , ZodiacSign.CANCER      , 28), impl.getFallStarDegree(ZodiacSign.CANCER     ).get());
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.LEO        ).orElse(null));
    assertEquals( new PointDegree(Planet.VENUS   , ZodiacSign.VIRGO       , 27), impl.getFallStarDegree(ZodiacSign.VIRGO      ).get());
    assertEquals( new PointDegree(Planet.SUN     , ZodiacSign.LIBRA       , 19), impl.getFallStarDegree(ZodiacSign.LIBRA      ).get());
    assertEquals( new PointDegree(Planet.MOON    , ZodiacSign.SCORPIO     ,  3), impl.getFallStarDegree(ZodiacSign.SCORPIO    ).get());
    //assertEquals( new PointDegree(LunarNode.NORTH     , ZodiacSign.SAGITTARIUS ,  3), impl.getFallStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals( new PointDegree(Planet.JUPITER , ZodiacSign.CAPRICORN   , 15), impl.getFallStarDegree(ZodiacSign.CAPRICORN  ).get());
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.AQUARIUS   ).orElse(null));
    assertEquals( new PointDegree(Planet.MERCURY , ZodiacSign.PISCES      , 15), impl.getFallStarDegree(ZodiacSign.PISCES     ).get());
    
  }

}
