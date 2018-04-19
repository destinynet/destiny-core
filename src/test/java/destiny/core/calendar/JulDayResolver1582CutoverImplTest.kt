/**
 * Created by smallufo on 2017-09-26.
 */
package destiny.core.calendar

import org.threeten.extra.chrono.JulianDate
import org.threeten.extra.chrono.JulianEra
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.IsoEra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class JulDayResolver1582CutoverImplTest {

  private val revJulDayFunc = {it:Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }

  @Test
  fun testFromDebugString() {
    assertEquals(LocalDateTime.of(2018, 4, 17, 18, 19, 30) , JulDayResolver1582CutoverImpl.fromDebugString("+20180417181930.0"))
    assertEquals(LocalDateTime.of(2018, 1, 2, 3, 4, 5) , JulDayResolver1582CutoverImpl.fromDebugString("+2018 1 2 3 4 5.0"))
    assertEquals(LocalDateTime.of(2018, 1, 2, 3, 4, 5 , 123_000_000) , JulDayResolver1582CutoverImpl.fromDebugString("+2018 1 2 3 4 5.123"))
    assertEquals(LocalDateTime.of(2018, 1, 2, 3, 4,59) , JulDayResolver1582CutoverImpl.fromDebugString("+2018 1 2 3 459.0"))
    assertEquals(LocalDateTime.of(2018, 1, 2, 3, 4,59) , JulDayResolver1582CutoverImpl.fromDebugString("+20180102030459.0"))
    assertEquals(LocalDateTime.of(2018, 1, 2, 3, 4,59, 155_000_000) , JulDayResolver1582CutoverImpl.fromDebugString("+20180102030459.155"))

    // Greg 開始
    val gregStart = LocalDateTime.of(1582, 10, 15, 0, 0)
    assertEquals(gregStart, JulDayResolver1582CutoverImpl.fromDebugString("+15821015000000.0"))
  }

  @Test
  fun getLocalDateTimeStatic() {
    val gregStart = LocalDateTime.of(1582, 10, 15, 0, 0)
    val date1 = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(gregStart.toInstant(ZoneOffset.UTC))

    val julianEnd = JulianDateTime.of(1582, 10, 5, 0, 0) // 此日期其實不存在於 julian date
    val date2 = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(julianEnd.toInstant(ZoneOffset.UTC))

    assertTrue(date2 is LocalDateTime)
    assertEquals(date1, date2)
  }

  /**
   * 從 julDay 傳回 LocalDate or JulianDate
   * 可以藉由這裡比對 http://aa.usno.navy.mil/data/docs/JulianDate.php
   */
  @Test
  fun julDay2DateTime_JulGreg_cutover() {

    // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
    val firstDayOfGregorian = 2299160.5

    var localDate: ChronoLocalDate
    var localTime: LocalTime

    // Gregorian 第一天 : 1582-10-15 , julDay = 2299160.5
    JulDayResolver1582CutoverImpl.getDateTime(firstDayOfGregorian).also {
      localDate = it.first
      localTime = it.second
      assertTrue(localDate is LocalDate)
      assertSame(IsoEra.CE, localDate.era)
      assertEquals(LocalDate.of(1582, 10, 15), localDate)
      assertEquals(LocalTime.MIDNIGHT, localTime)
    }


    // 1582-10-15 前一天 : 1582-10-04 ,  julDay = 2299159.5
    JulDayResolver1582CutoverImpl.getDateTime(firstDayOfGregorian - 1).also {
      localDate = it.first
      localTime = it.second
      assertTrue(localDate is JulianDate)
      assertSame(JulianEra.AD, localDate.era)
      assertEquals(JulianDate.of(1582, 10, 4), localDate)
      assertEquals(LocalTime.MIDNIGHT, localTime)
    }
      }


  /**
   * 西元元年
   * 從 julDay 傳回 LocalDate or JulianDate
   * 可以藉由這裡比對 http://aa.usno.navy.mil/data/docs/JulianDate.php
   */
  @Test
  fun testYear1() {
    //西元元年一月一號 (J)
    assertEquals(JulianDate.of(1, 1, 1), JulDayResolver1582CutoverImpl.getDateTime(1721423.5).first)

    //西元前一年十二月三十一號 (J)
    assertEquals(JulianDate.of(0, 12, 31), JulDayResolver1582CutoverImpl.getDateTime(1721422.5).first)

    //西元前一年一月一號 (J)
    assertEquals(JulianDate.of(0, 1, 1), JulDayResolver1582CutoverImpl.getDateTime(1721057.5).first)

    //西元前二年十二月三十一號 (J)
    assertEquals(JulianDate.of(-1, 12, 31), JulDayResolver1582CutoverImpl.getDateTime(1721056.5).first)
  }

  @Test
  fun julDay2DateTime_year1() {

    // 西元元年 , 一月一號 , 凌晨零時
    val firstDay = 1721423.5

    var localDate: ChronoLocalDate
    var localTime: LocalTime

    var dateTime: ChronoLocalDateTime<*> = revJulDayFunc.invoke(firstDay)
    localDate = dateTime.toLocalDate()
    localTime = dateTime.toLocalTime()
    assertTrue(localDate is JulianDate)
    assertSame(JulianEra.AD, localDate.era)
    assertEquals(JulianDate.of(1, 1, 1), localDate)
    assertEquals(LocalTime.MIDNIGHT, localTime)

    // 往前一天，變成 「西元前」一年，12/31
    dateTime = revJulDayFunc.invoke(firstDay - 1)
    localDate = dateTime.toLocalDate()
    localTime = dateTime.toLocalTime()
    assertTrue(localDate is JulianDate)
    assertSame(JulianEra.BC, localDate.era)
    assertEquals(JulianDate.of(0, 12, 31), localDate)
    assertEquals(LocalTime.MIDNIGHT, localTime)
  }

}