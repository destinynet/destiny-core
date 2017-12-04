/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.StarLucky.右弼
import destiny.core.chinese.ziwei.StarLucky.左輔
import destiny.core.chinese.ziwei.StarMain.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertSame

class TransFourFullCollectImplTest {

  private val impl = TransFourFullCollectImpl()

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
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
    assertSame(祿, impl.getValueOf(廉貞, 甲).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 乙).orElse(null))
    assertSame(忌, impl.getValueOf(廉貞, 丙).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 丁).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 戊).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 己).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 庚).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 辛).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 壬).orElse(null))
    assertSame(null, impl.getValueOf(廉貞, 癸).orElse(null))
  }
}