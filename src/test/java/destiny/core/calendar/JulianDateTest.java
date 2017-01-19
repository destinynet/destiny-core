/**
 * Created by smallufo on 2017-01-19.
 */
package destiny.core.calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class JulianDateTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Julian day epoch 測試
   * 已知： epoch 位於
   * January 1, 4713 BC 中午 :  proleptic Julian calendar
   * November 24, 4714 BC    : proleptic Gregorian calendar
   *
   */
  @Test
  public void testAstroJulDayNumber() {
    JulianDate jd = JulianDate.of(-4712 , 1 , 1);
    LocalDate gd = LocalDate.of(-4713 , 11 , 24);

    assertEquals(jd.toEpochDay() , gd.toEpochDay());
    logger.info("start of julian day , epoch = {}" , jd.toEpochDay());

  }

  @Test
  public void testBC() {
    JulianDate jd = JulianDate.of(1,1,1);
    JulianDate jd2 = jd.minus(1 , ChronoUnit.DAYS);
    assertEquals(1 , jd2.getYearOfEra());// 西元前一年 , 輸出為 1
    assertEquals(0 , jd2.getProlepticYear());// 西元前一年 , 連續值為 0
    logger.info("jd2 = {} . getYearOfEra = {} , getProlepticYear = {}" , jd2 , jd2.getYearOfEra() , jd2.getProlepticYear());

    // 西元1年1月1日，減一天 , 就是 0年12月31日 (proleptic)
    JulianDate jd3 = JulianDate.create(0 , 12 , 31);
    assertEquals(jd3 , jd2);
  }

  /**
   * 測試 JulianDate 與 GregorianDate 的 epochDay
   *
   * 利用這裡比對 http://www.stevemorse.org/jcal/julian.html
   *
   * 測試資料：
   * https://en.wikipedia.org/wiki/Conversion_between_Julian_and_Gregorian_calendars
   */
  @Test
  public void testEpochDay() {
    // 已知 G(2017, 1, 19) => J(2017,1,6)
    assertEquals(LocalDate.of(2017, 1, 19).toEpochDay() , JulianDate.of(2017,1,6).toEpochDay());

    // 西元前 501年
    assertEquals(JulianDate.of(-500,3,5).toEpochDay() , LocalDate.of(-500,2,28).toEpochDay());
    assertEquals(JulianDate.of(-500,3,6).toEpochDay() , LocalDate.of(-500,3, 1).toEpochDay());

    // 西元前 301年
    assertEquals(JulianDate.of(-300,3,3).toEpochDay() , LocalDate.of(-300,2,27).toEpochDay());
    assertEquals(JulianDate.of(-300,3,4).toEpochDay() , LocalDate.of(-300,2,28).toEpochDay());
    assertEquals(JulianDate.of(-300,3,5).toEpochDay() , LocalDate.of(-300,3,1).toEpochDay());

    // 西元前 201年
    assertEquals(JulianDate.of(-200,3,2).toEpochDay() , LocalDate.of(-200,2,27).toEpochDay());
    assertEquals(JulianDate.of(-200,3,3).toEpochDay() , LocalDate.of(-200,2,28).toEpochDay());
    assertEquals(JulianDate.of(-200,3,4).toEpochDay() , LocalDate.of(-200,3,1).toEpochDay());

    // 西元前 101年
    assertEquals(JulianDate.of(-100,3,1).toEpochDay() , LocalDate.of(-100,2,27).toEpochDay());
    assertEquals(JulianDate.of(-100,3,2).toEpochDay() , LocalDate.of(-100,2,28).toEpochDay());
    assertEquals(JulianDate.of(-100,3,3).toEpochDay() , LocalDate.of(-100,3,1).toEpochDay());


    assertEquals(JulianDate.of(100,2,29).toEpochDay() , LocalDate.of(100,2,27).toEpochDay());
    assertEquals(JulianDate.of(100,3,1).toEpochDay()  , LocalDate.of(100,2,28).toEpochDay());
    assertEquals(JulianDate.of(100,3,2).toEpochDay()  , LocalDate.of(100,3,1).toEpochDay());

    assertEquals(JulianDate.of(200,2,28).toEpochDay() , LocalDate.of(200,2,27).toEpochDay());
    assertEquals(JulianDate.of(200,2,29).toEpochDay() , LocalDate.of(200,2,28).toEpochDay());
    assertEquals(JulianDate.of(200,3,1).toEpochDay()  , LocalDate.of(200,3,1).toEpochDay());  // 日期相同 , 往下維持 100年！？

    assertEquals(JulianDate.of(300,2,28).toEpochDay() , LocalDate.of(300,2,28).toEpochDay()); // 日期相同
    assertEquals(JulianDate.of(300,2,29).toEpochDay() , LocalDate.of(300,3,1).toEpochDay());
    assertEquals(JulianDate.of(300,3,1).toEpochDay()  , LocalDate.of(300,3,2).toEpochDay());

    assertEquals(JulianDate.of(500,2,28).toEpochDay() , LocalDate.of(500,3,1).toEpochDay());
    assertEquals(JulianDate.of(500,2,29).toEpochDay() , LocalDate.of(500,3,2).toEpochDay());
    assertEquals(JulianDate.of(500,3,1).toEpochDay()  , LocalDate.of(500,3,3).toEpochDay());

    assertEquals(JulianDate.of(500,2,28).toEpochDay() , LocalDate.of(500,3,1).toEpochDay());
    assertEquals(JulianDate.of(500,2,29).toEpochDay() , LocalDate.of(500,3,2).toEpochDay());
    assertEquals(JulianDate.of(500,3,1).toEpochDay()  , LocalDate.of(500,3,3).toEpochDay());

    assertEquals(JulianDate.of(600,2,28).toEpochDay() , LocalDate.of(600,3,2).toEpochDay());
    assertEquals(JulianDate.of(600,2,29).toEpochDay() , LocalDate.of(600,3,3).toEpochDay());
    assertEquals(JulianDate.of(600,3,1).toEpochDay()  , LocalDate.of(600,3,4).toEpochDay());

    assertEquals(JulianDate.of(700,2,28).toEpochDay() , LocalDate.of(700,3,3).toEpochDay());
    assertEquals(JulianDate.of(700,2,29).toEpochDay() , LocalDate.of(700,3,4).toEpochDay());
    assertEquals(JulianDate.of(700,3,1).toEpochDay()  , LocalDate.of(700,3,5).toEpochDay());

    assertEquals(JulianDate.of(900,2,28).toEpochDay() , LocalDate.of(900,3,4).toEpochDay());
    assertEquals(JulianDate.of(900,2,29).toEpochDay() , LocalDate.of(900,3,5).toEpochDay());
    assertEquals(JulianDate.of(900,3,1).toEpochDay()  , LocalDate.of(900,3,6).toEpochDay());

    assertEquals(JulianDate.of(1000,2,28).toEpochDay() , LocalDate.of(1000,3,5).toEpochDay());
    assertEquals(JulianDate.of(1000,2,29).toEpochDay() , LocalDate.of(1000,3,6).toEpochDay());
    assertEquals(JulianDate.of(1000,3,1).toEpochDay()  , LocalDate.of(1000,3,7).toEpochDay());

    assertEquals(JulianDate.of(1100,2,28).toEpochDay() , LocalDate.of(1100,3,6).toEpochDay());
    assertEquals(JulianDate.of(1100,2,29).toEpochDay() , LocalDate.of(1100,3,7).toEpochDay());
    assertEquals(JulianDate.of(1100,3,1).toEpochDay()  , LocalDate.of(1100,3,8).toEpochDay());

    assertEquals(JulianDate.of(1300,2,28).toEpochDay() , LocalDate.of(1300,3,7).toEpochDay());
    assertEquals(JulianDate.of(1300,2,29).toEpochDay() , LocalDate.of(1300,3,8).toEpochDay());
    assertEquals(JulianDate.of(1300,3, 1).toEpochDay() , LocalDate.of(1300,3,9).toEpochDay());

    assertEquals(JulianDate.of(1400,2,28).toEpochDay() , LocalDate.of(1400,3,8).toEpochDay());
    assertEquals(JulianDate.of(1400,2,29).toEpochDay() , LocalDate.of(1400,3,9).toEpochDay());
    assertEquals(JulianDate.of(1400,3, 1).toEpochDay() , LocalDate.of(1400,3,10).toEpochDay());

    assertEquals(JulianDate.of(1500,2,28).toEpochDay() , LocalDate.of(1500,3, 9).toEpochDay());
    assertEquals(JulianDate.of(1500,2,29).toEpochDay() , LocalDate.of(1500,3,10).toEpochDay());
    assertEquals(JulianDate.of(1500,3, 1).toEpochDay() , LocalDate.of(1500,3,11).toEpochDay());

    // 曆法 cutover : (J)1582-10-4 下一天 => (G)1582-10-15

    assertEquals(JulianDate.of(1582,10,4).toEpochDay() , LocalDate.of(1582,10,14).toEpochDay());
                                                                   // ^^^^^^^^^^ 歷史上其實此日期(含)之前不存在
                            // ^^^^^^^^^ 1582-10-4 ，隔天就是 1582-10-15
    assertEquals(JulianDate.of(1582,10,5).toEpochDay() , LocalDate.of(1582,10,15).toEpochDay());
                            // ^^^^^^^^^ 歷史上此日期（含）之後不存在
    assertEquals(JulianDate.of(1582,10,6).toEpochDay() , LocalDate.of(1582,10,16).toEpochDay());


    assertEquals(JulianDate.of(1700,2,18).toEpochDay() , LocalDate.of(1700,2,28).toEpochDay());
    assertEquals(JulianDate.of(1700,2,19).toEpochDay() , LocalDate.of(1700,3, 1).toEpochDay());
    assertEquals(JulianDate.of(1700,2,28).toEpochDay() , LocalDate.of(1700,3,10).toEpochDay());
    assertEquals(JulianDate.of(1700,2,29).toEpochDay() , LocalDate.of(1700,3,11).toEpochDay());
    assertEquals(JulianDate.of(1700,3, 1).toEpochDay() , LocalDate.of(1700,3,12).toEpochDay());

    assertEquals(JulianDate.of(1800,2,17).toEpochDay() , LocalDate.of(1800,2,28).toEpochDay());
    assertEquals(JulianDate.of(1800,2,18).toEpochDay() , LocalDate.of(1800,3, 1).toEpochDay());
    assertEquals(JulianDate.of(1800,2,28).toEpochDay() , LocalDate.of(1800,3,11).toEpochDay());
    assertEquals(JulianDate.of(1800,2,29).toEpochDay() , LocalDate.of(1800,3,12).toEpochDay());
    assertEquals(JulianDate.of(1800,3, 1).toEpochDay() , LocalDate.of(1800,3,13).toEpochDay());

    assertEquals(JulianDate.of(1900,2,16).toEpochDay() , LocalDate.of(1900,2,28).toEpochDay());
    assertEquals(JulianDate.of(1900,2,17).toEpochDay() , LocalDate.of(1900,3, 1).toEpochDay());
    assertEquals(JulianDate.of(1900,2,28).toEpochDay() , LocalDate.of(1900,3,12).toEpochDay());
    assertEquals(JulianDate.of(1900,2,29).toEpochDay() , LocalDate.of(1900,3,13).toEpochDay());
    assertEquals(JulianDate.of(1900,3, 1).toEpochDay() , LocalDate.of(1900,3,14).toEpochDay());

    assertEquals(JulianDate.of(2100,2,15).toEpochDay() , LocalDate.of(2100,2,28).toEpochDay());
    assertEquals(JulianDate.of(2100,2,16).toEpochDay() , LocalDate.of(2100,3, 1).toEpochDay());
    assertEquals(JulianDate.of(2100,2,28).toEpochDay() , LocalDate.of(2100,3,13).toEpochDay());
    assertEquals(JulianDate.of(2100,2,29).toEpochDay() , LocalDate.of(2100,3,14).toEpochDay());
  }


  @Test
  public void testFromEpochDay() {
    JulianDate jd = JulianDate.ofEpochDay(LocalDate.of(2100,3,14).toEpochDay());
    assertEquals(2, jd.getMonth() );
    assertEquals(29, jd.getDayOfMonth() );
  }
}