/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarMain.太陽
import destiny.core.chinese.ziwei.T4Value.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class TransFourZiyunImplTest {

  internal var impl: ITransFour = TransFourZiyunImpl()


  /** 各家四化流派的名稱（i18n）。原本只有 logger.info，翻譯掉了也不會紅 */
  @Test
  fun testTitle() {
    assertEquals("紫雲", impl.getTitle(Locale.TAIWAN))
    assertEquals("紫云", impl.getTitle(Locale.CHINA))
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
