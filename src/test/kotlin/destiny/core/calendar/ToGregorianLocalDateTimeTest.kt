/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ToGregorianLocalDateTimeTest {

  @Test
  fun `格里曆原樣回傳`() {
    val t = LocalDateTime.of(1990, 5, 1, 12, 34, 56)
    assertSame(t, t.toGregorianLocalDateTime())
  }

  @Test
  fun `改曆當天：儒略曆 1582-10-04 的隔天是格里曆 1582-10-15`() {
    assertEquals(LocalDateTime.of(1582, 10, 14, 21, 0), JulianDateTime.of(1582, 10, 4, 21, 0, 0, 0).toGregorianLocalDateTime())
  }

  @Test
  fun `儒略曆有 1700-02-29、格里曆沒有：差距從 10 天變 11 天`() {
    assertEquals(LocalDateTime.of(1700, 3, 10, 8, 30), JulianDateTime.of(1700, 2, 28, 8, 30, 0, 0).toGregorianLocalDateTime())
    assertEquals(LocalDateTime.of(1700, 3, 11, 8, 30), JulianDateTime.of(1700, 2, 29, 8, 30, 0, 0).toGregorianLocalDateTime())
    assertEquals(LocalDateTime.of(1700, 3, 12, 8, 30), JulianDateTime.of(1700, 3, 1, 8, 30, 0, 0).toGregorianLocalDateTime())
  }

  @Test
  fun `西元前也一樣是同一天：儒略曆 BC 44-03-15（proleptic -43）`() {
    // 凱撒遇刺：儒略曆 BC 44-03-15 ＝ proleptic 格里曆 -43-03-13
    assertEquals(LocalDateTime.of(-43, 3, 13, 12, 0), JulianDateTime.of(-43, 3, 15, 12, 0, 0, 0).toGregorianLocalDateTime())
  }
}
