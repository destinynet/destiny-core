/**
 * Created by smallufo on 2020-03-13.
 */
package destiny.tools

import destiny.tools.StringTools.decodeToFinalUrl
import kotlin.test.Test
import kotlin.test.assertEquals


class StringToolsTest {

  /** 正常 */
  @Test
  fun normal() {
    assertEquals("ABC" , "ABC".decodeToFinalUrl())
    assertEquals("許功蓋" , "%E8%A8%B1%E5%8A%9F%E8%93%8B".decodeToFinalUrl())
    assertEquals("https://google.com" , "https%3A%2F%2Fgoogle.com".decodeToFinalUrl())
    assertEquals("line1\nline2" , "line1%0Aline2".decodeToFinalUrl())
  }

  /** TODO 包含空白 */
  @Test
  fun withSpace() {
    assertEquals("A B C" , "A%20B%20C".decodeToFinalUrl())
    assertEquals(" A B C " , "%20A%20B%20C%20".decodeToFinalUrl())
    assertEquals("許 功 蓋" , "%E8%A8%B1%20%E5%8A%9F%20%E8%93%8B".decodeToFinalUrl())
  }

  /** TODO 包含加號 */
  @Test
  fun withPlus() {
    assertEquals("A+B+C" , "A%2BB%2BC".decodeToFinalUrl())
    assertEquals("許+功+蓋 空白" , "%E8%A8%B1%2B%E5%8A%9F%2B%E8%93%8B%20%E7%A9%BA%E7%99%BD".decodeToFinalUrl())
  }
}
