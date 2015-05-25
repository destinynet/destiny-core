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

public class EssentialRedfDefaultImplTest
{
  /** 測試 Ruler (旺) */
  @Test
  public void testRuler()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.ARIES       , Dignity.RULER));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.TAURUS      , Dignity.RULER));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.GEMINI      , Dignity.RULER));
    assertSame(Planet.MOON    , impl.getPoint(ZodiacSign.CANCER      , Dignity.RULER));
    assertSame(Planet.SUN     , impl.getPoint(ZodiacSign.LEO         , Dignity.RULER));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.VIRGO       , Dignity.RULER));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.LIBRA       , Dignity.RULER));
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.RULER));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.RULER));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.RULER));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.RULER));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.PISCES      , Dignity.RULER));
  }
  
  /** 測試 Detriment (陷) , 其值為對沖星座之Ruler */
  @Test
  public void testDetriment()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.ARIES       , Dignity.DETRIMENT));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.TAURUS      , Dignity.DETRIMENT));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.DETRIMENT));
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.CANCER      , Dignity.DETRIMENT));
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.LEO         , Dignity.DETRIMENT));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.VIRGO       , Dignity.DETRIMENT));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.LIBRA       , Dignity.DETRIMENT));
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.DETRIMENT));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.DETRIMENT));
    assertSame(Planet.MOON      , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.DETRIMENT));
    assertSame(Planet.SUN       , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.DETRIMENT));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.PISCES      , Dignity.DETRIMENT));
  }

  /** 測試 Exaltation (廟) */
  @Test
  public void testExaltation()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    
    assertSame(Planet.SUN     , impl.getPoint(ZodiacSign.ARIES       , Dignity.EXALTATION));
    assertSame(Planet.MOON    , impl.getPoint(ZodiacSign.TAURUS      , Dignity.EXALTATION));
    //assertSame(LunarNode.NORTH , impl.getPoint(ZodiacSign.GEMINI      , Dignity.EXALTATION));
    assertSame(Planet.JUPITER , impl.getPoint(ZodiacSign.CANCER      , Dignity.EXALTATION));
    assertSame(null           , impl.getPoint(ZodiacSign.LEO         , Dignity.EXALTATION));
    assertSame(Planet.MERCURY , impl.getPoint(ZodiacSign.VIRGO       , Dignity.EXALTATION));
    assertSame(Planet.SATURN  , impl.getPoint(ZodiacSign.LIBRA       , Dignity.EXALTATION));
    assertSame(null           , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.EXALTATION));
    //assertSame(LunarNode.SOUTH , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.EXALTATION));
    assertSame(Planet.MARS    , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.EXALTATION));
    assertSame(null           , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.EXALTATION));
    assertSame(Planet.VENUS   , impl.getPoint(ZodiacSign.PISCES      , Dignity.EXALTATION));
  }
  
  /** 測試 Fall (落) , 其值為對沖星座之Exaltation之星體 */
  @Test
  public void testFall()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    
    assertSame(Planet.SATURN    , impl.getPoint(ZodiacSign.ARIES       , Dignity.FALL));
    assertSame(null             , impl.getPoint(ZodiacSign.TAURUS      , Dignity.FALL));
    //assertSame(LunarNode.SOUTH   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.FALL));
    assertSame(Planet.MARS      , impl.getPoint(ZodiacSign.CANCER      , Dignity.FALL));
    assertSame(null             , impl.getPoint(ZodiacSign.LEO         , Dignity.FALL));
    assertSame(Planet.VENUS     , impl.getPoint(ZodiacSign.VIRGO       , Dignity.FALL));
    assertSame(Planet.SUN       , impl.getPoint(ZodiacSign.LIBRA       , Dignity.FALL));
    assertSame(Planet.MOON      , impl.getPoint(ZodiacSign.SCORPIO     , Dignity.FALL));
    //assertSame(LunarNode.NORTH   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.FALL));
    assertSame(Planet.JUPITER   , impl.getPoint(ZodiacSign.CAPRICORN   , Dignity.FALL));
    assertSame(null             , impl.getPoint(ZodiacSign.AQUARIUS    , Dignity.FALL));
    assertSame(Planet.MERCURY   , impl.getPoint(ZodiacSign.PISCES      , Dignity.FALL));
  }

  
  /**
   * 測試星座的 Exaltation 星體及度數
   */
  @Test
  public void testGetExaltationStarDegree()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    assertEquals( new PointDegree(Planet.SUN     , ZodiacSign.ARIES       , 19), impl.getExaltationStarDegree(ZodiacSign.ARIES      ));
    assertEquals( new PointDegree(Planet.MOON    , ZodiacSign.TAURUS      ,  3), impl.getExaltationStarDegree(ZodiacSign.TAURUS     ));
    //assertEquals( new PointDegree(LunarNode.NORTH , ZodiacSign.GEMINI      ,  3), impl.getExaltationStarDegree(ZodiacSign.GEMINI     ));
    assertEquals( new PointDegree(Planet.JUPITER , ZodiacSign.CANCER      , 15), impl.getExaltationStarDegree(ZodiacSign.CANCER     ));
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.LEO        ));
    assertEquals( new PointDegree(Planet.MERCURY , ZodiacSign.VIRGO       , 15), impl.getExaltationStarDegree(ZodiacSign.VIRGO      ));
    assertEquals( new PointDegree(Planet.SATURN  , ZodiacSign.LIBRA       , 21), impl.getExaltationStarDegree(ZodiacSign.LIBRA      ));
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.SCORPIO    ));
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.SAGITTARIUS ,  3), impl.getExaltationStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals( new PointDegree(Planet.MARS    , ZodiacSign.CAPRICORN   , 28), impl.getExaltationStarDegree(ZodiacSign.CAPRICORN  ));
    assertEquals( null                                                        , impl.getExaltationStarDegree(ZodiacSign.AQUARIUS   ));
    assertEquals( new PointDegree(Planet.VENUS   , ZodiacSign.PISCES      , 27), impl.getExaltationStarDegree(ZodiacSign.PISCES     ));
  }

  @Test
  public void testGetFallStarDegree()
  {
    EssentialRedfDefaultImpl impl = new EssentialRedfDefaultImpl();
    assertEquals( new PointDegree(Planet.SATURN  , ZodiacSign.ARIES       , 21), impl.getFallStarDegree(ZodiacSign.ARIES      ));
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.TAURUS     ));
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.GEMINI      ,  3), impl.getFallStarDegree(ZodiacSign.GEMINI     ));
    assertEquals( new PointDegree(Planet.MARS    , ZodiacSign.CANCER      , 28), impl.getFallStarDegree(ZodiacSign.CANCER     ));
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.LEO        ));
    assertEquals( new PointDegree(Planet.VENUS   , ZodiacSign.VIRGO       , 27), impl.getFallStarDegree(ZodiacSign.VIRGO      ));
    assertEquals( new PointDegree(Planet.SUN     , ZodiacSign.LIBRA       , 19), impl.getFallStarDegree(ZodiacSign.LIBRA      ));
    assertEquals( new PointDegree(Planet.MOON    , ZodiacSign.SCORPIO     ,  3), impl.getFallStarDegree(ZodiacSign.SCORPIO    ));
    //assertEquals( new PointDegree(LunarNode.NORTH     , ZodiacSign.SAGITTARIUS ,  3), impl.getFallStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals( new PointDegree(Planet.JUPITER , ZodiacSign.CAPRICORN   , 15), impl.getFallStarDegree(ZodiacSign.CAPRICORN  ));
    assertEquals( null                                                        , impl.getFallStarDegree(ZodiacSign.AQUARIUS   ));
    assertEquals( new PointDegree(Planet.MERCURY , ZodiacSign.PISCES      , 15), impl.getFallStarDegree(ZodiacSign.PISCES     ));
    
  }

}
