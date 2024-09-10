/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.T4Value.忌
import destiny.core.chinese.ziwei.T4Value.科
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class TransFourMiddleImplTest {

  internal var impl: ITransFour = TransFourMiddleImpl()

  private val logger = KotlinLogging.logger { }

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {

    assertSame(太陽, impl.getStarOf(戊, 科))
    assertSame(天機, impl.getStarOf(戊, 忌))

    assertSame(天府, impl.getStarOf(庚, 科))
    assertSame(天同, impl.getStarOf(庚, 忌))

    assertSame(天府, impl.getStarOf(壬, 科))
    assertSame(武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertNull(impl.getValueOf(天府, 甲))
    assertNull(impl.getValueOf(天府, 乙))
    assertNull(impl.getValueOf(天府, 丙))
    assertNull(impl.getValueOf(天府, 丁))
    assertNull(impl.getValueOf(天府, 戊))
    assertNull(impl.getValueOf(天府, 己))
    assertSame(科, impl.getValueOf(天府, 庚))
    assertNull(impl.getValueOf(天府, 辛))
    assertSame(科, impl.getValueOf(天府, 壬))
    assertNull(impl.getValueOf(天府, 癸))
  }
}
