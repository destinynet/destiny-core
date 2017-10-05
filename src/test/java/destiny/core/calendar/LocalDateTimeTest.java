/**
 * Created by smallufo on 2015-05-14.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import static java.lang.System.out;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LocalDateTimeTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testWithValue() {
    LocalDateTime ldt = LocalDateTime.now();
    logger.info("before withYear , ldt = {}" , ldt);
    LocalDateTime ldt2 = ldt.withYear(2000);
    logger.info("after withYear , ldt = {}" , ldt);
    logger.info("ldt2 = {}" , ldt2);
  }


  @Test
  public void testOutput() {
    logger.info("{}" , LocalDate.of(2012 , 6, 21).format(DateTimeFormatter.ofPattern("uuuu-MM-dd")));
    logger.info("{}" , LocalDate.of(2012 , 6, 21).format(DateTimeFormatter.ofPattern("uuuu-MM")));
  }

  /**
   * LocalDateTime 並未考慮 cutover 狀況
   *
   */
  @Test
  public void testLocalDateTime1582() {
    LocalDateTime ldt = LocalDateTime.of(1582, 10 , 16 , 0 , 0);
    //TimeZone tz = TimeZone.getTimeZone("America/New_York");
    TimeZone tz = TimeZone.getTimeZone("Asia/Taipei");
    ZonedDateTime zdt = ldt.atZone(tz.toZoneId());
    for(int i=0 ; i <10 ; i++) {
      zdt = ZonedDateTime.from(zdt).minusDays(1);
      logger.info("zdt = {}" , zdt);
    }
  }

  /**
   * 西元元年之前
   * <p>
0001-01-02 : era = CE , year = 1 , year_of_era = 1 , toEpochDay = -719161
0001-01-01 : era = CE , year = 1 , year_of_era = 1 , toEpochDay = -719162
0000-12-31 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719163
0000-12-30 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719164
0000-12-29 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719165
0000-12-28 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719166
   * </p>
   */
  @Test
  public void test_LocalDate_BC() {
    LocalDate ld = LocalDate.of(1 , 1 , 3);
    // 西元 1年 1月 3日

    ld = ld.minusDays(1);
    // 西元 1年 1月 2日
    assertSame(IsoEra.CE , ld.getEra());
    assertEquals(1 , ld.getYear());
    assertEquals(1 , ld.get(YEAR_OF_ERA));
    assertEquals(1 , ld.getMonthValue());
    assertEquals(-719161 , ld.toEpochDay());

    ld = ld.minusDays(1);
    // 西元 1年 1月 1日
    assertSame(IsoEra.CE , ld.getEra());
    assertEquals(1 , ld.getYear());
    assertEquals(1 , ld.get(YEAR_OF_ERA));
    assertEquals(1 , ld.getMonthValue());
    assertEquals(-719162 , ld.toEpochDay());

    ld = ld.minusDays(1);
    // 西元前 1年 12月 31日
    assertSame(IsoEra.BCE , ld.getEra());
    assertEquals(0 , ld.getYear());         // year 是連續的，因此「西元前一年」， year = 0
    assertEquals(1 , ld.get(YEAR_OF_ERA));  // year_of_era 一定大於0
    assertEquals(12 , ld.getMonthValue());
    assertEquals(-719163 , ld.toEpochDay());

    ld = ld.minusDays(1);
    // 西元前 1年 12月 30日
    assertSame(IsoEra.BCE , ld.getEra());
    assertEquals(0 , ld.getYear());         // year 是連續的，因此「西元前一年」， year = 0
    assertEquals(1 , ld.get(YEAR_OF_ERA));  // year_of_era 一定大於0
    assertEquals(12 , ld.getMonthValue());
    assertEquals(-719164 , ld.toEpochDay());


  }

  /**
   * 比對 year , 以及 year_of_era 的差異
0001-01-02T00:00 : era = CE , year = 1 , year_of_era = 1
0001-01-01T00:00 : era = CE , year = 1 , year_of_era = 1
0000-12-31T00:00 : era = BCE , year = 0 , year_of_era = 1
0000-12-30T00:00 : era = BCE , year = 0 , year_of_era = 1
0000-12-29T00:00 : era = BCE , year = 0 , year_of_era = 1
0000-12-28T00:00 : era = BCE , year = 0 , year_of_era = 1
   */
  @Test
  public void test_LocalDateTime_BC() {
    LocalDateTime ldt = LocalDateTime.of(1 , 1 , 3 , 0 , 0);
    for(int i=1 ; i <= 6 ; i++) {
      ldt = ldt.minusDays(1);
      logger.info("{} : era = {} , year = {} , year_of_era = {}" ,
        ldt , ldt.toLocalDate().getEra() , ldt.getYear() , ldt.get(YEAR_OF_ERA) );
    }
  }

  @Test
  public void testEra_Compare() {
    LocalDateTime now = LocalDateTime.now();
    assertSame(IsoEra.CE , now.toLocalDate().getEra()); // 現在應該是西元後

    LocalDateTime ce = LocalDateTime.of(1 , 1 , 1 , 0 , 0 , 0); // 西元第一秒
    assertSame(IsoEra.CE , ce.toLocalDate().getEra());

    LocalDateTime bce = LocalDateTime.from(ce).minusSeconds(1); // 西元前最後一秒
    assertSame(IsoEra.BCE , bce.toLocalDate().getEra());
  }


  @Test
  public void testEpochSecond() {
    LocalDateTime ldt = LocalDateTime.of(1970 , 1 , 1 , 0 , 0);
    out.println("ldt = " + ldt);
    assertEquals(0, ldt.atZone(ZoneId.of("GMT")).toEpochSecond());
    assertEquals(-60*60*8, ldt.atZone(ZoneId.of("Asia/Taipei")).toEpochSecond());

  }


  @Test
  public void testEra() {
    LocalDate localDate;

    localDate = LocalDate.of(1 , 1 , 1);
    assertSame(IsoEra.CE , localDate.getEra());

    localDate = localDate.minus(1 , ChronoUnit.DAYS);
    assertSame(IsoEra.BCE , localDate.getEra());
  }

}
