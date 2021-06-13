/**
 * Created by smallufo on 2021-06-13.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.EightWords
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PersonCoreCanvas3x24Test {

  @Test
  fun testR2L() {
    val canvas = PersonCoreCanvas3x24(Gender.男, EightWords("甲子", "乙丑", "丙寅", "丁卯"), Direction.R2L)
    val expected = """
|　　　　男　　　　　　　
|　丁　　丙　　乙　　甲　
|　卯　　寅　　丑　　子　
""".trimMargin("|")
    assertEquals(expected, canvas.monoOutput)
    println(canvas.htmlOutput)
  }

  @Test
  fun testL2R() {
    val canvas = PersonCoreCanvas3x24(Gender.女, EightWords("甲子", "乙丑", "丙寅", "丁卯"), Direction.L2R)
    println(canvas.monoOutput)
    val expected = """
|　　　　　　　女　　　　
|　甲　　乙　　丙　　丁　
|　子　　丑　　寅　　卯　
""".trimMargin("|")
    assertEquals(expected, canvas.monoOutput)
    println(canvas.htmlOutput)
  }
}
