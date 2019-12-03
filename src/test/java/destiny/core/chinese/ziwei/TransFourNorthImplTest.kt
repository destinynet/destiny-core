/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.StarLucky.右弼
import destiny.core.chinese.ziwei.StarLucky.左輔
import destiny.core.chinese.ziwei.StarMain.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class TransFourNorthImplTest {

  internal var impl: ITransFour = TransFourNorthImpl()

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
    assertSame(天相, impl.getStarOf(庚, 忌))

    assertSame(左輔, impl.getStarOf(壬, 科))
    assertSame(武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertSame(科, impl.getValueOf(武曲, 甲))
    assertNull(impl.getValueOf(武曲, 乙))
    assertNull(impl.getValueOf(武曲, 丙))
    assertNull(impl.getValueOf(武曲, 丁))
    assertNull(impl.getValueOf(武曲, 戊))
    assertSame(祿, impl.getValueOf(武曲, 己))
    assertSame(權, impl.getValueOf(武曲, 庚))
    assertNull(impl.getValueOf(武曲, 辛))
    assertSame(忌, impl.getValueOf(武曲, 壬))
    assertNull(impl.getValueOf(武曲, 癸))
  }
}
