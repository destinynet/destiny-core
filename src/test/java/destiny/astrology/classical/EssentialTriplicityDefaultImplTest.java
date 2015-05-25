/**
 * @author smallufo 
 * Created on 2007/12/12 at 下午 3:10:54
 */ 
package destiny.astrology.classical;

import destiny.astrology.DayNight;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class EssentialTriplicityDefaultImplTest
{

  @Test
  public void testGetStar()
  {
    EssentialTriplicityIF impl = new EssentialTriplicityDefaultImpl();
    
    assertSame(Planet.SUN   , impl.getTriplicityPoint(ZodiacSign.ARIES       , DayNight.DAY) );
    assertSame(Planet.VENUS , impl.getTriplicityPoint(ZodiacSign.TAURUS      , DayNight.DAY) );
    assertSame(Planet.SATURN, impl.getTriplicityPoint(ZodiacSign.GEMINI      , DayNight.DAY) );
    assertSame(Planet.MARS  , impl.getTriplicityPoint(ZodiacSign.CANCER      , DayNight.DAY) );
    assertSame(Planet.SUN   , impl.getTriplicityPoint(ZodiacSign.LEO         , DayNight.DAY) );
    assertSame(Planet.VENUS , impl.getTriplicityPoint(ZodiacSign.VIRGO       , DayNight.DAY) );
    assertSame(Planet.SATURN, impl.getTriplicityPoint(ZodiacSign.LIBRA       , DayNight.DAY) );
    assertSame(Planet.MARS  , impl.getTriplicityPoint(ZodiacSign.SCORPIO     , DayNight.DAY) );
    assertSame(Planet.SUN   , impl.getTriplicityPoint(ZodiacSign.SAGITTARIUS , DayNight.DAY) );
    assertSame(Planet.VENUS , impl.getTriplicityPoint(ZodiacSign.CAPRICORN   , DayNight.DAY) );
    assertSame(Planet.SATURN, impl.getTriplicityPoint(ZodiacSign.AQUARIUS    , DayNight.DAY) );
    assertSame(Planet.MARS  , impl.getTriplicityPoint(ZodiacSign.PISCES      , DayNight.DAY) );
    
    assertSame(Planet.JUPITER , impl.getTriplicityPoint(ZodiacSign.ARIES       , DayNight.NIGHT) );
    assertSame(Planet.MOON    , impl.getTriplicityPoint(ZodiacSign.TAURUS      , DayNight.NIGHT) );
    assertSame(Planet.MERCURY , impl.getTriplicityPoint(ZodiacSign.GEMINI      , DayNight.NIGHT) );
    assertSame(Planet.MARS    , impl.getTriplicityPoint(ZodiacSign.CANCER      , DayNight.NIGHT) );
    assertSame(Planet.JUPITER , impl.getTriplicityPoint(ZodiacSign.LEO         , DayNight.NIGHT) );
    assertSame(Planet.MOON    , impl.getTriplicityPoint(ZodiacSign.VIRGO       , DayNight.NIGHT) );
    assertSame(Planet.MERCURY , impl.getTriplicityPoint(ZodiacSign.LIBRA       , DayNight.NIGHT) );
    assertSame(Planet.MARS    , impl.getTriplicityPoint(ZodiacSign.SCORPIO     , DayNight.NIGHT) );
    assertSame(Planet.JUPITER , impl.getTriplicityPoint(ZodiacSign.SAGITTARIUS , DayNight.NIGHT) );
    assertSame(Planet.MOON    , impl.getTriplicityPoint(ZodiacSign.CAPRICORN   , DayNight.NIGHT) );
    assertSame(Planet.MERCURY , impl.getTriplicityPoint(ZodiacSign.AQUARIUS    , DayNight.NIGHT) );
    assertSame(Planet.MARS    , impl.getTriplicityPoint(ZodiacSign.PISCES      , DayNight.NIGHT) );
  }

}
