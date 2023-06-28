/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import java.time.LocalTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HourMinSecDecoratorTest {

  val decorator = HourMinSecDecorator

  @Test
  fun tradChinese() {
    assertEquals("00時00分 00.00秒", decorator.getOutputString(LocalTime.of(0, 0, 0, 0), Locale.TAIWAN))
    assertEquals("00時01分 00.00秒", decorator.getOutputString(LocalTime.of(0, 1, 0, 0), Locale.TAIWAN))
    assertEquals("00時00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 0), Locale.TAIWAN))
    assertEquals("00時00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 1), Locale.TAIWAN))
    assertEquals("00時00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 10), Locale.TAIWAN))
    assertEquals("00時00分 59.00秒", decorator.getOutputString(LocalTime.of(0, 0, 59, 0), Locale.TAIWAN))
    assertEquals("23時59分 59.99秒", decorator.getOutputString(LocalTime.of(23, 59, 59, 999000000), Locale.TAIWAN))
    assertEquals("23時59分 59.99秒", decorator.getOutputString(LocalTime.of(23, 59, 59, 999999999), Locale.TAIWAN))
  }

  @Test
  fun simpChinese() {
    assertEquals("00时00分 00.00秒", decorator.getOutputString(LocalTime.of(0, 0, 0, 0), Locale.SIMPLIFIED_CHINESE))
    assertEquals("00时01分 00.00秒", decorator.getOutputString(LocalTime.of(0, 1, 0, 0), Locale.SIMPLIFIED_CHINESE))
    assertEquals("00时00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 0), Locale.SIMPLIFIED_CHINESE))
    assertEquals("00时00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 1), Locale.SIMPLIFIED_CHINESE))
    assertEquals("00时00分 01.00秒", decorator.getOutputString(LocalTime.of(0, 0, 1, 10), Locale.SIMPLIFIED_CHINESE))
    assertEquals("00时00分 59.00秒", decorator.getOutputString(LocalTime.of(0, 0, 59, 0), Locale.SIMPLIFIED_CHINESE))
    assertEquals("23时59分 59.99秒", decorator.getOutputString(LocalTime.of(23, 59, 59, 999000000), Locale.SIMPLIFIED_CHINESE))
    assertEquals("23时59分 59.99秒", decorator.getOutputString(LocalTime.of(23, 59, 59, 999999999), Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun english() {
    assertEquals("00:00:00.00", decorator.getOutputString(LocalTime.of(0, 0, 0, 0), Locale.ENGLISH))
    assertEquals("00:01:00.00", decorator.getOutputString(LocalTime.of(0, 1, 0, 0), Locale.ENGLISH))
    assertEquals("00:00:01.00", decorator.getOutputString(LocalTime.of(0, 0, 1, 0), Locale.ENGLISH))
    assertEquals("00:00:01.00", decorator.getOutputString(LocalTime.of(0, 0, 1, 1), Locale.ENGLISH))
    assertEquals("00:00:01.00", decorator.getOutputString(LocalTime.of(0, 0, 1, 10), Locale.ENGLISH))
    assertEquals("00:00:59.00", decorator.getOutputString(LocalTime.of(0, 0, 59, 0), Locale.ENGLISH))
    assertEquals("23:59:59.99", decorator.getOutputString(LocalTime.of(23, 59, 59, 999000000), Locale.ENGLISH))
    assertEquals("23:59:59.99", decorator.getOutputString(LocalTime.of(23, 59, 59, 999999999), Locale.ENGLISH))
  }
}
