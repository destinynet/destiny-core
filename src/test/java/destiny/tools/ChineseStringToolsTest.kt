/**
 * Created by smallufo on 2018-06-11.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals


class ChineseStringToolsTest {

  @Test
  fun alignRight() {
    assertEquals("哈囉" , ChineseStringTools.alignRight("哈囉" , 4))
    assertEquals("　哈囉" , ChineseStringTools.alignRight("哈囉" , 6))
    assertEquals("　　哈囉" , ChineseStringTools.alignRight("哈囉" , 8))
  }
}