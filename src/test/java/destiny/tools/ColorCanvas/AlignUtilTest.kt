/**
 * @author smallufo
 * Created on 2007/3/24 at 上午 12:04:18
 */
package destiny.tools.ColorCanvas

import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class AlignUtilTest {

  private val logger = LoggerFactory.getLogger(javaClass)


  @Test
  fun testAlignRight_1() {
    val value = 1
    assertEquals(" 1", AlignUtil.alignRight(value, 2))
    assertEquals("　 1", AlignUtil.alignRight(value, 4))
    assertEquals("　　 1", AlignUtil.alignRight(value, 6))
  }

  @Test
  fun testAlignRight_12() {
    val value = 12
    assertEquals("12", AlignUtil.alignRight(value, 2))
    assertEquals("　12", AlignUtil.alignRight(value, 4))
    assertEquals("　　12", AlignUtil.alignRight(value, 6))
  }

  @Test
  fun testAlignRight_0() {
    val value = 0
    assertEquals(" 0", AlignUtil.alignRight(value, 2))
    assertEquals("　 0", AlignUtil.alignRight(value, 4))
    assertEquals("　　 0", AlignUtil.alignRight(value, 6))
  }
}
