/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.calendar.chinese

import destiny.core.chinese.StemBranch
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ChineseDateKtTest {

  @Test
  fun testToChineseMonthString() {
    assertEquals("正月", 1.toChineseMonthString())
    assertEquals("二月", 2.toChineseMonthString())
    assertEquals("九月", 9.toChineseMonthString())
    assertEquals("十月", 10.toChineseMonthString())
    assertEquals("十一月", 11.toChineseMonthString())
    assertEquals("十二月", 12.toChineseMonthString())
    try {
      0.toChineseMonthString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such month : 0" , e.message)
    }
    try {
      13.toChineseMonthString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such month : 13" , e.message)
    }
  }

  @Test
  fun testToChineseDayString_appendDayFalse() {
    assertEquals("初一", 1.toChineseDayString())
    assertEquals("初十", 10.toChineseDayString())
    assertEquals("十一", 11.toChineseDayString())
    assertEquals("十九", 19.toChineseDayString())
    assertEquals("二十", 20.toChineseDayString())
    assertEquals("廿一", 21.toChineseDayString())
    assertEquals("廿九", 29.toChineseDayString())
    assertEquals("三十", 30.toChineseDayString())
    try {
      0.toChineseDayString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such day : 0" , e.message)
    }
    try {
      31.toChineseDayString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such day : 31" , e.message)
    }
  }

  @Test
  fun testToChineseDayString_appendDayTrue() {
    assertEquals("初一", 1.toChineseDayString(true))
    assertEquals("初十", 10.toChineseDayString(true))
    assertEquals("十一日", 11.toChineseDayString(true))
    assertEquals("十九日", 19.toChineseDayString(true))
    assertEquals("二十日", 20.toChineseDayString(true))
    assertEquals("廿一日", 21.toChineseDayString(true))
    assertEquals("廿九日", 29.toChineseDayString(true))
    assertEquals("三十日", 30.toChineseDayString(true))
    try {
      0.toChineseDayString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such day : 0" , e.message)
    }
    try {
      31.toChineseDayString()
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No such day : 31" , e.message)
    }
  }

  fun testDisplay_appendDayFalse(): Stream<Pair<ChineseDate, String>> = Stream.of(
    ChineseDate(null, StemBranch.庚子, 1, false, 1) to "庚子年正月初一",
    ChineseDate(null, StemBranch.庚子, 4, false, 1) to "庚子年四月初一",
    ChineseDate(null, StemBranch.庚子, 4, false, 10) to "庚子年四月初十",
    ChineseDate(null, StemBranch.庚子, 4, false, 20) to "庚子年四月二十",
    ChineseDate(null, StemBranch.庚子, 4, false, 29) to "庚子年四月廿九",
    ChineseDate(null, StemBranch.庚子, 4, false, 30) to "庚子年四月三十",

    ChineseDate(null, StemBranch.庚子, 4, true, 1) to "庚子年閏四月初一",
    ChineseDate(null, StemBranch.庚子, 4, true, 10) to "庚子年閏四月初十",
    ChineseDate(null, StemBranch.庚子, 4, true, 20) to "庚子年閏四月二十",
    ChineseDate(null, StemBranch.庚子, 4, true, 29) to "庚子年閏四月廿九",
    ChineseDate(null, StemBranch.庚子, 4, true, 30) to "庚子年閏四月三十",

    ChineseDate(null, StemBranch.庚子, 12, false, 1) to "庚子年十二月初一",
    ChineseDate(null, StemBranch.庚子, 12, false, 29) to "庚子年十二月廿九",
    ChineseDate(null, StemBranch.庚子, 12, false, 30) to "庚子年十二月三十",

    ChineseDate(null, StemBranch.庚子, 12, true, 1) to "庚子年閏十二月初一",
    ChineseDate(null, StemBranch.庚子, 12, true, 29) to "庚子年閏十二月廿九",
    ChineseDate(null, StemBranch.庚子, 12, true, 30) to "庚子年閏十二月三十",
  )

  @ParameterizedTest
  @MethodSource
  fun testDisplay_appendDayFalse(row: Pair<ChineseDate, String>) {
    assertEquals(row.second, row.first.display())
  }

  fun testDisplay_appendDayTrue(): Stream<Pair<ChineseDate, String>> = Stream.of(
    ChineseDate(null, StemBranch.庚子, 1, false, 1) to "庚子年正月初一",
    ChineseDate(null, StemBranch.庚子, 4, false, 1) to "庚子年四月初一",
    ChineseDate(null, StemBranch.庚子, 4, false, 10) to "庚子年四月初十",
    ChineseDate(null, StemBranch.庚子, 4, false, 20) to "庚子年四月二十日",
    ChineseDate(null, StemBranch.庚子, 4, false, 29) to "庚子年四月廿九日",
    ChineseDate(null, StemBranch.庚子, 4, false, 30) to "庚子年四月三十日",

    ChineseDate(null, StemBranch.庚子, 4, true, 1) to "庚子年閏四月初一",
    ChineseDate(null, StemBranch.庚子, 4, true, 10) to "庚子年閏四月初十",
    ChineseDate(null, StemBranch.庚子, 4, true, 20) to "庚子年閏四月二十日",
    ChineseDate(null, StemBranch.庚子, 4, true, 29) to "庚子年閏四月廿九日",
    ChineseDate(null, StemBranch.庚子, 4, true, 30) to "庚子年閏四月三十日",

    ChineseDate(null, StemBranch.庚子, 12, false, 1) to "庚子年十二月初一",
    ChineseDate(null, StemBranch.庚子, 12, false, 29) to "庚子年十二月廿九日",
    ChineseDate(null, StemBranch.庚子, 12, false, 30) to "庚子年十二月三十日",

    ChineseDate(null, StemBranch.庚子, 12, true, 1) to "庚子年閏十二月初一",
    ChineseDate(null, StemBranch.庚子, 12, true, 29) to "庚子年閏十二月廿九日",
    ChineseDate(null, StemBranch.庚子, 12, true, 30) to "庚子年閏十二月三十日",
  )

  @ParameterizedTest
  @MethodSource
  fun testDisplay_appendDayTrue(row: Pair<ChineseDate, String>) {
    assertEquals(row.second, row.first.display(appendDay = true))
  }
}
