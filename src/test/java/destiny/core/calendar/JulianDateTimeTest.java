/**
 * Created by smallufo on 2017-10-03.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.chrono.JulianDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

import static org.junit.Assert.assertEquals;

public class JulianDateTimeTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 1970-01-01 0:00 GMT ,  epoch sec = 0
   * 轉為 Julian date , 減去 14天
   */
  @Test
  public void ofEpochSecond() throws Exception {
    JulianDateTime jdt = JulianDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    assertEquals(JulianDateTime.of(1969, 12, 19, 0, 0), jdt);
    assertEquals(JulianDate.of(1969, 12, 19).atTime(LocalTime.of(0, 0)), jdt);
  }


  /**
   * 西元 1970-01-01 的 Jul Greg 互相轉換 (相差14天)
   * <pre>
   *    Greg          Julian
   * 1970-01-14     1970-01-01
   * </pre>
   */
  @Test
  public void testEpoch_Jul2Greg() {
    JulianDateTime jd = JulianDateTime.of(1970, 1, 1, 0, 0);
    // Julian -> Greg
    assertEquals(LocalDateTime.of(1970, 1, 14, 0, 0), LocalDateTime.from(jd));
  }


  /**
   * 西元 1970-01-01 的 Jul Greg 互相轉換 (相差14天)
   * <pre>
   *    Greg          Julian
   * 1970-01-01     1969-12-19
   * </pre>
   */
  @Test
  public void testEpoch_Greg2Jul() {
    LocalDateTime gregTime = LocalDateTime.of(1970, 1, 1, 0, 0);

    ChronoLocalDateTime fromGregTime = JulianDateTime.from(gregTime);
    JulianDateTime julianTime = JulianDateTime.of(1969, 12, 19, 0, 0);

    // Greg -> Julian
    assertEquals(fromGregTime, julianTime);
  }
}