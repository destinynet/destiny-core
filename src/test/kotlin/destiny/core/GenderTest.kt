/**
 * Created by Claude on 2026-10-16.
 */
package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * 性別字串 → enum 全站一份。之前 13 處各自寫 `if (s == "M") M else F`，而且預設方向不一致
 * （base 的表單未知→M、LIFF 未知→F）。這裡不猜：不認得就 null，預設由呼叫端明寫。
 */
class GenderTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Gender::class, false)
  }

  @Test
  fun `M F 不分大小寫、容忍前後空白`() {
    assertEquals(Gender.M, Gender.of("M"))
    assertEquals(Gender.F, Gender.of("f"))
    assertEquals(Gender.M, Gender.of(" m "))
  }

  @Test
  fun `不認得、空白、null 都是 null —— 不替呼叫端猜預設`() {
    assertNull(Gender.of("male"))
    assertNull(Gender.of("X"))
    assertNull(Gender.of(""))
    assertNull(Gender.of(null))
  }

  @Test
  fun `code 與 of 互逆`() {
    Gender.entries.forEach { assertEquals(it, Gender.of(it.code)) }
    assertEquals("M", Gender.M.code)
    assertEquals("F", Gender.F.code)
  }
}
