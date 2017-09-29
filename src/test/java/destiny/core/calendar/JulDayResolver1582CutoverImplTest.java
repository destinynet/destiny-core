/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import org.threeten.extra.chrono.JulianDate;
import org.threeten.extra.chrono.JulianEra;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoEra;

import static org.junit.Assert.*;

public class JulDayResolver1582CutoverImplTest {



  @Test
  public void julDay2DateTime_JulGreg_cutover() throws Exception {

    // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
    double firstDayOfGregorian = 2299160.5;

    ChronoLocalDate localDate;
    LocalTime localTime;

    // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
    Tuple2<ChronoLocalDate , LocalTime> dateTime1 = JulDayResolver1582CutoverImpl.getDateTime(firstDayOfGregorian);
    localDate = dateTime1.v1();
    localTime = dateTime1.v2();
    assertTrue(localDate instanceof LocalDate);
    assertSame(IsoEra.CE , localDate.getEra());
    assertEquals(LocalDate.of(1582,10,15) , localDate);
    assertEquals(LocalTime.MIDNIGHT , localTime);


    // 1582-10-15 前一天 : 1582-10-04 ,  julDay = 2299159.5
    Tuple2<ChronoLocalDate , LocalTime> dateTime2 = JulDayResolver1582CutoverImpl.getDateTime(firstDayOfGregorian-1);
    localDate = dateTime2.v1();
    localTime = dateTime2.v2();
    assertTrue(localDate instanceof JulianDate);
    assertSame(JulianEra.AD, localDate.getEra());
    assertEquals(JulianDate.of(1582,10,4) , localDate);
    assertEquals(LocalTime.MIDNIGHT , localTime);
  }

  @Test
  public void julDay2DateTime_year1() {

    // 西元元年 , 一月一號 , 凌晨零時
    double firstDay = 1721423.5;

    ChronoLocalDate localDate;
    LocalTime localTime;

    ChronoLocalDateTime dateTime = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(firstDay);
    localDate = dateTime.toLocalDate();
    localTime = dateTime.toLocalTime();
    assertTrue(localDate instanceof JulianDate);
    assertSame(JulianEra.AD, localDate.getEra());
    assertEquals(JulianDate.of(1,1,1) , localDate);
    assertEquals(LocalTime.MIDNIGHT , localTime);

    // 往前一天，變成 「西元前」一年，12/31
    dateTime = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(firstDay-1);
    localDate = dateTime.toLocalDate();
    localTime = dateTime.toLocalTime();
    assertTrue(localDate instanceof JulianDate);
    assertSame(JulianEra.BC, localDate.getEra());
    assertEquals(JulianDate.of(0,12,31) , localDate);
    assertEquals(LocalTime.MIDNIGHT , localTime);
  }

}