/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import destiny.tools.Lang
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class NaYinTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testNaYin() {
    StemBranch.entries.forEach { sb ->
      assertNotNull(NaYin.getFiveElement(sb))
      assertNotNull(NaYin.getDesc(sb, Lang.ZH_TW))
      logger.info("{} : {} , 簡體 : {} , 五行 : {}" ,
        sb.toString() , NaYin.getDesc(sb, Lang.ZH_TW) , NaYin.getDesc(sb, Lang.ZH_CN) , NaYin.getFiveElement(sb))
    }
  }
}
