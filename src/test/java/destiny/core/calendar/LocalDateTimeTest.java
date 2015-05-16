/**
 * Created by smallufo on 2015-05-14.
 */
package destiny.core.calendar;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.JulianFields;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

public class LocalDateTimeTest {

  /**
   * 1582/10/4 之後跳到 1582/10/15 , 之前是 Julian Calendar , 之後是 Gregorian Calendar
   * 測試 10/5~10/14 之間的錯誤日期
   */
  @Test
  public void testJulian2Gregorian() {
    LocalDate ld = LocalDate.of(1582, 10, 17);
    ZoneId zone = ZoneId.of("GMT");
    ZonedDateTime zdt;

    for (int i = 1; i <= 15; i++) {
      ld = ld.minusDays(1);
      zdt = ld.atTime(0 , 0).atZone(zone);
      out.print(ld + " , jd = " + ld.getLong(JulianFields.JULIAN_DAY));
      //out.print(" , zdt's jd = " + zdt.getLong(JulianFields.JULIAN_DAY));
      out.println();
    }
  }

  /**
   * 西元元年之前
   * <p>
   * 0001-01-02 , era = CE , toEpochDay = -719161
   * 0001-01-01 , era = CE , toEpochDay = -719162
   * 0000-12-31 , era = BCE , toEpochDay = -719163
   * 0000-12-30 , era = BCE , toEpochDay = -719164
   * 0000-12-29 , era = BCE , toEpochDay = -719165
   * 0000-12-28 , era = BCE , toEpochDay = -719166
   */
  @Test
  public void testBC() {
    LocalDate ld = LocalDate.of(1 , 1 , 3);
    for(int i=1 ; i <= 6 ; i++) {
      ld = ld.minusDays(1);
      out.println(ld + " , era = " + ld.getEra() + " , toEpochDay = " + ld.toEpochDay());
    }
  }

  @Test
  public void testEpochSecond() {
    LocalDateTime ldt = LocalDateTime.of(1970 , 1 , 1 , 0 , 0);
    out.println("ldt = " + ldt);
    assertEquals(0, ldt.atZone(ZoneId.of("GMT")).toEpochSecond());
    assertEquals(-60*60*8, ldt.atZone(ZoneId.of("Asia/Taipei")).toEpochSecond());

  }


  @Test
  public void testOld() {
    GregorianCalendar gc = new GregorianCalendar(1582 , 9-1 , 30);
    for(int i = 1 ; i <= 10 ; i++) {
      gc.add(Calendar.DAY_OF_YEAR , 1);
      LocalDate ld = gc.toZonedDateTime().toLocalDate();
      out.print("[GC]" + gc.get(Calendar.YEAR) + "-" + (gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DAY_OF_MONTH) + " \t-> ");
      out.println("[LocalDate]"+ld);
    }
  }

}
