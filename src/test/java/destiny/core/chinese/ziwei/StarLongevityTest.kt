/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender.女
import destiny.core.Gender.男
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.YinYangIF.陰
import destiny.core.chinese.YinYangIF.陽
import destiny.core.chinese.ziwei.StarLongevity.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class StarLongevityTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (star in StarLongevity.values) {
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("tw = {}({}) , cn = {}({})",
        star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }

  @Test
  fun testRun() {
    assertSame(申, fun長生.apply(水, 男, 陽))
    assertSame(戌, fun沐浴.apply(木, 男, 陰))
    assertSame(未, fun冠帶.apply(金, 女, 陰))
    assertSame(午, fun胎.apply(土, 女, 陰))
    assertSame(卯, fun養.apply(火, 男, 陰))
  }
}
