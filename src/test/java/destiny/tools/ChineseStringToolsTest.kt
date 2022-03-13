/**
 * Created by smallufo on 2018-06-11.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals


class ChineseStringToolsTest {

  @Test
  fun tesTtoBiggerDigits() {
    assertEquals("０", ChineseStringTools.toBiggerDigits(0))
    assertEquals("１", ChineseStringTools.toBiggerDigits(1))
  }

  @Test
  fun testReplaceToBiggerDigits() {
    assertEquals("哈囉１２３" , ChineseStringTools.replaceToBiggerDigits("哈囉123"))
    assertEquals("１哈２囉３" , ChineseStringTools.replaceToBiggerDigits("1哈2囉3"))
  }

  @Test
  fun alignRight() {
    assertEquals("哈囉" , ChineseStringTools.alignRight("哈囉" , 4))
    assertEquals("　哈囉" , ChineseStringTools.alignRight("哈囉" , 6))
    assertEquals("　　哈囉" , ChineseStringTools.alignRight("哈囉" , 8))
  }
}
