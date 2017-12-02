/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.忌
import destiny.core.chinese.ziwei.ITransFour.Value.科
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class TransFourMiddleImplTest {

  internal var impl: ITransFour = TransFourMiddleImpl()

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {

    assertSame(StarMain.太陽, impl.getStarOf(戊, 科))
    assertSame(StarMain.天機, impl.getStarOf(戊, 忌))

    assertSame(StarMain.天府, impl.getStarOf(庚, 科))
    assertSame(StarMain.天同, impl.getStarOf(庚, 忌))

    assertSame(StarMain.天府, impl.getStarOf(壬, 科))
    assertSame(StarMain.武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertNull(impl.getValueOf(StarMain.天府, 甲).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 乙).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 丙).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 丁).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 戊).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 己).orElse(null))
    assertSame(科, impl.getValueOf(StarMain.天府, 庚).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 辛).orElse(null))
    assertSame(科, impl.getValueOf(StarMain.天府, 壬).orElse(null))
    assertNull(impl.getValueOf(StarMain.天府, 癸).orElse(null))
  }
}