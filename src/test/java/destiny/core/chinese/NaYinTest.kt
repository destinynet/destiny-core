/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class NaYinTest {

  @Test
  fun testNaYin() {
    for (sb in StemBranch.iterable()) {
      assertNotNull(sb)
      assertNotNull(NaYin.getFiveElement(sb))
      assertNotNull(NaYin.getDesc(sb, Locale.TAIWAN))
      println(sb.toString() + " : " + NaYin.getDesc(sb, Locale.TAIWAN) + " , 簡體 : " + NaYin.getDesc(sb , Locale.SIMPLIFIED_CHINESE) + " , 五行 : " + NaYin.getFiveElement(sb))
    }
  }
}