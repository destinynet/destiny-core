/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.getAbbreviation
import destiny.astrology.toString
import destiny.core.Gender.女
import destiny.core.Gender.男
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.YinYang.陰
import destiny.core.chinese.YinYang.陽
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun冠帶
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun沐浴
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun胎
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun長生
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun養
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StarLongevityTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testToString() {
    assertEquals("長生", StarLongevity.長生.toString(Locale.TAIWAN))
    assertEquals("长生", StarLongevity.長生.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("長生", StarLongevity.長生.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("生", StarLongevity.長生.getAbbreviation(Locale.TAIWAN))
    assertEquals("生", StarLongevity.長生.getAbbreviation(Locale.SIMPLIFIED_CHINESE))

    assertEquals("養", StarLongevity.養.getAbbreviation(Locale.TAIWAN))
    assertEquals("养", StarLongevity.養.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testToStrings() {
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
