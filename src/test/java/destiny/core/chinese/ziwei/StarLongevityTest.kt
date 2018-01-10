/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender.女
import destiny.core.Gender.男
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.IYinYang.陰
import destiny.core.chinese.IYinYang.陽
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun冠帶
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun沐浴
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun胎
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun長生
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun養
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

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
    assertSame(申, fun長生.invoke(水, 男, 陽))
    assertSame(戌, fun沐浴.invoke(木, 男, 陰))
    assertSame(未, fun冠帶.invoke(金, 女, 陰))
    assertSame(午, fun胎.invoke(土, 女, 陰))
    assertSame(卯, fun養.invoke(火, 男, 陰))
  }
}
