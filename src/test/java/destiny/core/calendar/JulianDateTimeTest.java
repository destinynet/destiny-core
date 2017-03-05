/**
 * Created by smallufo on 2017-01-20.
 */
package destiny.core.calendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class JulianDateTimeTest {

  @Test
  public void testJulDay() {

    // 中午 , 0
    assertEquals(2457774, Time.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 12, 0)), 0.0);

    // 下午 6點，過了 0.25天
    assertEquals(2457774.25, Time.getGmtJulDay(JulianDateTime.of(2017, 1, 7, 18, 0)), 0.0);

    // 晚上12點，過了 0.5天
    assertEquals(2457774.5, Time.getGmtJulDay(JulianDateTime.of(2017, 1, 8, 0, 0)), 0.0);   // (g)1/21

    // 隔天早上 6點，過了 0.75天
    assertEquals(2457774.75, Time.getGmtJulDay(JulianDateTime.of(2017, 1, 8, 6, 0)), 0.0);
  }
}