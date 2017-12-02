/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class TransFourFullBookImplTest {

  internal var impl: ITransFour = TransFourFullBookImpl()

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testTitle() {
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  fun getStarOf() {
    assertSame(StarLucky.右弼, impl.getStarOf(戊, 科))
    assertSame(StarMain.天機, impl.getStarOf(戊, 忌))

    assertSame(StarMain.天同, impl.getStarOf(庚, 科))
    assertSame(StarMain.太陰, impl.getStarOf(庚, 忌))

    assertSame(StarMain.天府, impl.getStarOf(壬, 科))
    assertSame(StarMain.武曲, impl.getStarOf(壬, 忌))
  }

  @Test
  fun getValueOf() {
    assertNull(impl.getValueOf(StarMain.天機, 甲).orElse(null))
    assertSame(祿, impl.getValueOf(StarMain.天機, 乙).orElse(null))
    assertSame(權, impl.getValueOf(StarMain.天機, 丙).orElse(null))
    assertSame(科, impl.getValueOf(StarMain.天機, 丁).orElse(null))
    assertSame(忌, impl.getValueOf(StarMain.天機, 戊).orElse(null))
    assertNull(impl.getValueOf(StarMain.天機, 己).orElse(null))
    assertNull(impl.getValueOf(StarMain.天機, 庚).orElse(null))
    assertNull(impl.getValueOf(StarMain.天機, 辛).orElse(null))
    assertNull(impl.getValueOf(StarMain.天機, 壬).orElse(null))
    assertNull(impl.getValueOf(StarMain.天機, 癸).orElse(null))
  }
}