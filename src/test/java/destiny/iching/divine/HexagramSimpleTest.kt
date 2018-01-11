/**
 * @author smallufo
 * Created on 2011/4/18 at 下午12:30:43
 */
package destiny.iching.divine

import destiny.iching.HexagramSimple
import kotlin.test.Test

class HexagramSimpleTest {
  @Test
  fun testRun() {
    val lines1 = arrayOf(true, true, true, true, true, true)
    println(HexagramSimple.getIndex(lines1))

    val lines2 = arrayOf(false, false, false, false, false, false)
    println(HexagramSimple.getIndex(lines2))

    val lines64 = arrayOf(false, true, false, true, false, true)
    println(HexagramSimple.getIndex(lines64))
  }
}
