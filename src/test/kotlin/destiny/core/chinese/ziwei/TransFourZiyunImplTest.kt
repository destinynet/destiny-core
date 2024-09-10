/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarMain.太陽
import destiny.core.chinese.ziwei.T4Value.*
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class TransFourZiyunImplTest {

  internal var impl: ITransFour = TransFourZiyunImpl()

  private val logger = KotlinLogging.logger { }

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {
    assertSame(太陽, impl.getStarOf(癸, 科))
  }

  @Test
  fun getValueOf() {
    assertSame(忌, impl.getValueOf(太陽, 甲))
    assertNull(impl.getValueOf(太陽, 乙))
    assertNull(impl.getValueOf(太陽, 丙))
    assertNull(impl.getValueOf(太陽, 丁))
    assertNull(impl.getValueOf(太陽, 戊))
    assertNull(impl.getValueOf(太陽, 己))
    assertSame(祿, impl.getValueOf(太陽, 庚))
    assertSame(權, impl.getValueOf(太陽, 辛))
    assertNull(impl.getValueOf(太陽, 壬))
    assertSame(科, impl.getValueOf(太陽, 癸))
  }
}
