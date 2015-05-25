/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 5:27:05
 */ 
package destiny.astrology.classical;

import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EssentialTermsDefaultImplTest
{

  @Test
  public void testGetStarDouble()
  {
    EssentialTermsIF impl = new EssentialTermsDefaultImpl();
    //戌
    assertTrue(impl.getTermsStar(0) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(5.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(6) == Planet.VENUS);
    assertTrue(impl.getTermsStar(14) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(21) == Planet.MARS);
    assertTrue(impl.getTermsStar(26) == Planet.SATURN);
    //酉
    assertTrue(impl.getTermsStar(30) == Planet.VENUS);
    assertTrue(impl.getTermsStar(37.99) == Planet.VENUS);
    assertTrue(impl.getTermsStar(56) == Planet.MARS);
    //申
    assertTrue(impl.getTermsStar(60) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(80.99) == Planet.VENUS);
    
    //亥
    assertTrue(impl.getTermsStar(330) == Planet.VENUS);
    assertTrue(impl.getTermsStar(338) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(344) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(350) == Planet.MARS);
    assertTrue(impl.getTermsStar(355.99) == Planet.MARS);
    assertTrue(impl.getTermsStar(356) == Planet.SATURN);
    assertTrue(impl.getTermsStar(359.99) == Planet.SATURN);
    //破 360度 , 回到 戌
    assertTrue(impl.getTermsStar(360) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(365.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(386) == Planet.SATURN);
    //酉
    assertTrue(impl.getTermsStar(390) == Planet.VENUS);
    //破720 , 回到 戌
    assertTrue(impl.getTermsStar(720) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(725.99) == Planet.JUPITER);

    //度數小於0 , 往前推到亥
    assertTrue(impl.getTermsStar(-0.001) == Planet.SATURN);
    // -30 , 仍是亥
    assertTrue(impl.getTermsStar(-30) == Planet.VENUS);
    //-359 , 戌宮
    assertTrue(impl.getTermsStar(-359) == Planet.JUPITER);
  }

  @Test
  public void testGetStarZodiacSignDouble()
  {
    EssentialTermsIF impl = new EssentialTermsDefaultImpl();
    
    //戌
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 0) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 5.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 6) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 13.99) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 14) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 20.99) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 21) == Planet.MARS);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 25.99) == Planet.MARS);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 26) == Planet.SATURN);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 29.99) == Planet.SATURN);
    //戌 , 破30度，應該進到酉
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 30) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.ARIES , 37.99) == Planet.VENUS);
    //酉
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 0) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 7.99) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 8) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 14.99) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 15) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 21.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 22) == Planet.SATURN);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 25.99) == Planet.SATURN);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 26) == Planet.MARS);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 29.99) == Planet.MARS);
    //酉 , 破 30 度 , 應該進到申
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 30) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.TAURUS , 36.99) == Planet.MERCURY);
    
    //亥
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 0) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 7.99) == Planet.VENUS);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 8) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 13.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 14) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 19.99) == Planet.MERCURY);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 20) == Planet.MARS);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 25.99) == Planet.MARS);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 26) == Planet.SATURN);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 29.99) == Planet.SATURN);
    //破30 , 回到戌
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 30) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 35.99) == Planet.JUPITER);
    assertTrue(impl.getTermsStar(ZodiacSign.PISCES , 36) == Planet.VENUS);
  }

}
