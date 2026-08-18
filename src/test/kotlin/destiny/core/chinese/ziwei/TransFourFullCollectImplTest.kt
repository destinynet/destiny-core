/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarLucky.右弼
import destiny.core.chinese.ziwei.StarLucky.左輔
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.T4Value.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TransFourFullCollectImplTest {

  private val impl = TransFourFullCollectImpl()


  /** 各家四化流派的名稱（i18n）。原本只有 logger.info，翻譯掉了也不會紅 */
  @Test
  fun testTitle() {
    assertEquals("全集", impl.getTitle(Locale.TAIWAN))
    assertEquals("全集", impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {

    assertSame(右弼, impl.getStarOf(戊, 科))
    assertSame(天機, impl.getStarOf(戊, 忌))

    assertSame(太陰, impl.getStarOf(庚, 科))
    assertSame(天同, impl.getStarOf(庚, 忌))

    assertSame(左輔, impl.getStarOf(壬, 科))
    assertSame(武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertSame(祿, impl.getValueOf(廉貞, 甲))
    assertSame(null, impl.getValueOf(廉貞, 乙))
    assertSame(忌, impl.getValueOf(廉貞, 丙))
    assertSame(null, impl.getValueOf(廉貞, 丁))
    assertSame(null, impl.getValueOf(廉貞, 戊))
    assertSame(null, impl.getValueOf(廉貞, 己))
    assertSame(null, impl.getValueOf(廉貞, 庚))
    assertSame(null, impl.getValueOf(廉貞, 辛))
    assertSame(null, impl.getValueOf(廉貞, 壬))
    assertSame(null, impl.getValueOf(廉貞, 癸))
  }
}
