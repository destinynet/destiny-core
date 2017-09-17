/**
 * Created by smallufo on 2017-09-18.
 */
package destiny.astrology;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HoroscopeTest {

  @Test
  public void testGetAngle() {
    assertTrue(Horoscope2.getAngle(1, 0) == 1);
    assertTrue(Horoscope2.getAngle(179, 0) == 179);
    assertTrue(Horoscope2.getAngle(180, 0) == 180);
    assertTrue(Horoscope2.getAngle(181, 0) == 179);
    assertTrue(Horoscope2.getAngle(359, 0) == 1);

    assertTrue(Horoscope2.getAngle(0, 1) == 1);
    assertTrue(Horoscope2.getAngle(0, 179) == 179);
    assertTrue(Horoscope2.getAngle(0, 180) == 180);
    assertTrue(Horoscope2.getAngle(0, 181) == 179);
    assertTrue(Horoscope2.getAngle(0, 359) == 1);

    assertTrue(Horoscope2.getAngle(270, 90) == 180);
    assertTrue(Horoscope2.getAngle(271, 90) == 179);
    assertTrue(Horoscope2.getAngle(359, 90) == 91);
    assertTrue(Horoscope2.getAngle(0, 90) == 90);
    assertTrue(Horoscope2.getAngle(89, 90) == 1);

    assertTrue(Horoscope2.getAngle(90, 270) == 180);
    assertTrue(Horoscope2.getAngle(90, 271) == 179);
    assertTrue(Horoscope2.getAngle(90, 359) == 91);
    assertTrue(Horoscope2.getAngle(90, 0) == 90);
    assertTrue(Horoscope2.getAngle(90, 89) == 1);
  }

}