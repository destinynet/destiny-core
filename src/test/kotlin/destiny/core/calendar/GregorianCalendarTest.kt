/**
 * Created by smallufo on 2017-10-03.
 */
package destiny.core.calendar

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GregorianCalendarTest {

  /**
   * 西元 1582 年，10-04 的隔天直接跳到 10-15（Julian → Gregorian 的 cutover）。
   *
   * [GregorianCalendar] 會處理這一段，故它的**欄位值**會跳過 10/05～10/14；
   * 而 `toLocalDate()` 轉出的 [LocalDate] 是 proleptic Gregorian，不理會 cutover，
   * 日期連續遞增 —— 兩者從 1582-10-15 之後才合流。
   *
   * 原本這裡只把兩欄印出來對眼睛看。
   */
  @Test
  fun test1582() {
    val gc = GregorianCalendar(1582, 9 - 1, 30)

    val gcFields = mutableListOf<String>()
    val localDates = mutableListOf<LocalDate>()

    repeat(10) {
      gc.add(Calendar.DAY_OF_YEAR, 1)
      gcFields += "%d-%02d-%02d".format(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH))
      localDates += gc.toZonedDateTime().toLocalDate()
    }

    // GregorianCalendar 的欄位值：10/04 之後直接跳到 10/15
    assertEquals(
      listOf("1582-10-01", "1582-10-02", "1582-10-03", "1582-10-04",
             "1582-10-15", "1582-10-16", "1582-10-17", "1582-10-18", "1582-10-19", "1582-10-20"),
      gcFields
    )

    // LocalDate（proleptic Gregorian）：連續遞增，不跳號
    assertEquals((11..20).map { LocalDate.of(1582, 10, it) }, localDates)

    // cutover 之後兩者一致
    assertEquals(gcFields.drop(4), localDates.drop(4).map { it.toString() })
  }
}
