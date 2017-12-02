/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.StarMain.太陽
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class TransFourZiyunImplTest {

  internal var impl: ITransFour = TransFourZiyunImpl()
  private val logger = LoggerFactory.getLogger(javaClass)

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
    assertSame(忌, impl.getValueOf(太陽, 甲).orElse(null))
    assertNull(impl.getValueOf(太陽, 乙).orElse(null))
    assertNull(impl.getValueOf(太陽, 丙).orElse(null))
    assertNull(impl.getValueOf(太陽, 丁).orElse(null))
    assertNull(impl.getValueOf(太陽, 戊).orElse(null))
    assertNull(impl.getValueOf(太陽, 己).orElse(null))
    assertSame(祿, impl.getValueOf(太陽, 庚).orElse(null))
    assertSame(權, impl.getValueOf(太陽, 辛).orElse(null))
    assertNull(impl.getValueOf(太陽, 壬).orElse(null))
    assertSame(科, impl.getValueOf(太陽, 癸).orElse(null))
  }
}