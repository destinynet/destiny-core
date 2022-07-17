/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarLucky.右弼
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.T4Value.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class TransFourFullBookImplTest {

  internal var impl: ITransFour = TransFourFullBookImpl()

  private val logger = KotlinLogging.logger { }

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {
    assertSame(右弼, impl.getStarOf(戊, 科))
    assertSame(天機, impl.getStarOf(戊, 忌))

    assertSame(天同, impl.getStarOf(庚, 科))
    assertSame(太陰, impl.getStarOf(庚, 忌))

    assertSame(天府, impl.getStarOf(壬, 科))
    assertSame(武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertNull(impl.getValueOf(天機, 甲))
    assertSame(祿, impl.getValueOf(天機, 乙))
    assertSame(權, impl.getValueOf(天機, 丙))
    assertSame(科, impl.getValueOf(天機, 丁))
    assertSame(忌, impl.getValueOf(天機, 戊))
    assertNull(impl.getValueOf(天機, 己))
    assertNull(impl.getValueOf(天機, 庚))
    assertNull(impl.getValueOf(天機, 辛))
    assertNull(impl.getValueOf(天機, 壬))
    assertNull(impl.getValueOf(天機, 癸))
  }
}
