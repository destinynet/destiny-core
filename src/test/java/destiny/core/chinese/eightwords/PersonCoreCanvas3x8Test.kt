/**
 * Created by smallufo on 2021-06-13.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.EightWords
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PersonCoreCanvas3x8Test {

  @Test
  fun testR2L() {
    val canvas = PersonCoreCanvas3x8(Gender.男, EightWords("甲子", "乙丑", "丙寅", "丁卯"), Direction.R2L)

    val expected = """
      　男　　
      丁丙乙甲
      卯寅丑子
    """.trimIndent()

    assertEquals(expected, canvas.monoOutput)
  }

  @Test
  fun testL2R() {
    val canvas = PersonCoreCanvas3x8(Gender.女, EightWords("甲子", "乙丑", "丙寅", "丁卯"), Direction.L2R)
    val expected = """
      　　女　
      甲乙丙丁
      子丑寅卯
    """.trimIndent()
    assertEquals(expected, canvas.monoOutput)
  }
}
