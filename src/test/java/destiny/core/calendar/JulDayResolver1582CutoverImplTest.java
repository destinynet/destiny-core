/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import org.threeten.extra.chrono.JulianDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JulDayResolver1582CutoverImplTest {

  // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
  double firstDayOfGregorian = 2299160.5;

  @Test
  public void julDay2DateTime() throws Exception {

    // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
    Tuple2<ChronoLocalDate , LocalTime> dateTime1 = JulDayResolver1582CutoverImpl.getDateAndTimeStatic(firstDayOfGregorian);
    assertTrue(dateTime1.v1() instanceof LocalDate);
    assertEquals(LocalDate.of(1582,10,15) , dateTime1.v1());
    assertEquals(LocalTime.MIDNIGHT , dateTime1.v2());


    // 1582-10-15 前一天 : 1582-10-04 ,  julDay = 2299159.5
    Tuple2<ChronoLocalDate , LocalTime> dateTime2 = JulDayResolver1582CutoverImpl.getDateAndTimeStatic(firstDayOfGregorian-1);
    assertTrue(dateTime2.v1() instanceof JulianDate);
    assertEquals(JulianDate.of(1582,10,4) , dateTime2.v1());
    assertEquals(LocalTime.MIDNIGHT , dateTime2.v2());
  }

}