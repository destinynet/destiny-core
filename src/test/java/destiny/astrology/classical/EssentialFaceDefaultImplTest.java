/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 9:20:46
 */ 
package destiny.astrology.classical;

import junit.framework.TestCase;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;

public class EssentialFaceDefaultImplTest extends TestCase
{
  public void testGetStarDouble()
  {
    EssentialFaceIF impl = new EssentialFaceDefaultImpl();
    
    assertTrue(impl.getFaceStar(0)==Planet.MARS);
    assertTrue(impl.getFaceStar(9.99)==Planet.MARS);
    assertTrue(impl.getFaceStar(10)==Planet.SUN);
    assertTrue(impl.getFaceStar(19.99)==Planet.SUN);
    assertTrue(impl.getFaceStar(20)==Planet.VENUS);
    assertTrue(impl.getFaceStar(20.99)==Planet.VENUS);
    
    assertTrue(impl.getFaceStar(30)==Planet.MERCURY);
    assertTrue(impl.getFaceStar(40)==Planet.MOON);
    assertTrue(impl.getFaceStar(50)==Planet.SATURN);
    
    assertTrue(impl.getFaceStar(60)==Planet.JUPITER);
    assertTrue(impl.getFaceStar(70)==Planet.MARS);
    assertTrue(impl.getFaceStar(80)==Planet.SUN);
    
    assertTrue(impl.getFaceStar( 90)==Planet.VENUS);
    assertTrue(impl.getFaceStar(100)==Planet.MERCURY);
    assertTrue(impl.getFaceStar(110)==Planet.MOON);
    
    assertTrue(impl.getFaceStar(120)==Planet.SATURN);
    assertTrue(impl.getFaceStar(130)==Planet.JUPITER);
    assertTrue(impl.getFaceStar(140)==Planet.MARS);
    
    assertTrue(impl.getFaceStar(150)==Planet.SUN);
    assertTrue(impl.getFaceStar(160)==Planet.VENUS);
    assertTrue(impl.getFaceStar(170)==Planet.MERCURY);
    
    assertTrue(impl.getFaceStar(180)==Planet.MOON);
    assertTrue(impl.getFaceStar(190)==Planet.SATURN);
    assertTrue(impl.getFaceStar(200)==Planet.JUPITER);
    
    assertTrue(impl.getFaceStar(210)==Planet.MARS);
    assertTrue(impl.getFaceStar(220)==Planet.SUN);
    assertTrue(impl.getFaceStar(230)==Planet.VENUS);
    
    assertTrue(impl.getFaceStar(240)==Planet.MERCURY);
    assertTrue(impl.getFaceStar(250)==Planet.MOON);
    assertTrue(impl.getFaceStar(260)==Planet.SATURN);
    
    assertTrue(impl.getFaceStar(270)==Planet.JUPITER);
    assertTrue(impl.getFaceStar(280)==Planet.MARS);
    assertTrue(impl.getFaceStar(290)==Planet.SUN);
    
    assertTrue(impl.getFaceStar(300)==Planet.VENUS);
    assertTrue(impl.getFaceStar(310)==Planet.MERCURY);
    assertTrue(impl.getFaceStar(320)==Planet.MOON);
    
    assertTrue(impl.getFaceStar(330)==Planet.SATURN);
    assertTrue(impl.getFaceStar(340)==Planet.JUPITER);
    assertTrue(impl.getFaceStar(350)==Planet.MARS);
    assertTrue(impl.getFaceStar(359.99)==Planet.MARS);
    //破360
    assertTrue(impl.getFaceStar(360)==Planet.MARS);
    assertTrue(impl.getFaceStar(361)==Planet.MARS);
    assertTrue(impl.getFaceStar(369.99)==Planet.MARS);
    assertTrue(impl.getFaceStar(720)==Planet.MARS);
    assertTrue(impl.getFaceStar(721)==Planet.MARS);
    assertTrue(impl.getFaceStar(729.99)==Planet.MARS);
    
  }

  public void testGetStarZodiacSignDouble()
  {
    EssentialFaceIF impl = new EssentialFaceDefaultImpl();

    //戌
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 0) == Planet.MARS);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 9.99) == Planet.MARS);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 10) == Planet.SUN);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 19.99) == Planet.SUN);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 20) == Planet.VENUS);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 29.99) == Planet.VENUS);
    //破30
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 30) == Planet.MERCURY);
    assertTrue(impl.getFaceStar(ZodiacSign.ARIES, 39.99) == Planet.MERCURY);
    
    //亥
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 0) == Planet.SATURN);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 9.99) == Planet.SATURN);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 10) == Planet.JUPITER);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 19.99) == Planet.JUPITER);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 20) == Planet.MARS);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 29.99) == Planet.MARS);
    //破30 , 回到戌
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 30) == Planet.MARS);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 39.990) == Planet.MARS);
    assertTrue(impl.getFaceStar(ZodiacSign.PISCES, 40) == Planet.SUN);
  }

}
