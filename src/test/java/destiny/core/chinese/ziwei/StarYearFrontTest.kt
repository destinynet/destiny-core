/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei

import destiny.core.astrology.getAbbreviation
import destiny.core.astrology.toString
import destiny.core.chinese.Branch.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StarYearFrontTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testToString() {
    assertEquals("龍德", StarYearFront.龍德.toString(Locale.TAIWAN))
    assertEquals("龙德", StarYearFront.龍德.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("龍德", StarYearFront.龍德.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("龍", StarYearFront.龍德.getAbbreviation(Locale.TAIWAN))
    assertEquals("龙", StarYearFront.龍德.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    //assertEquals("龍", StarYearFront.龍德.getAbbreviation(Locale.ENGLISH))
  }

  @Test
  fun testToStrings() {
    for (star in StarYearFront.values) {
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("tw = {}({}) , cn = {}({})",
        star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }

  /**
   * 比對 http://blog.xuite.net/paulwang0129/twblog/164106473
   */
  @Test
  fun testFun() {
    // 午年 , 歲建 在 午
    assertSame(午, StarYearFront.fun歲建.invoke(午))
    // 午年 , 晦氣 在 未
    assertSame(未, StarYearFront.fun晦氣.invoke(午))
    // 午年 , 歲破(大耗) 在 子
    assertSame(子, StarYearFront.fun歲破.invoke(午))
    // 午年 , 病符 在 巳
    assertSame(巳, StarYearFront.fun病符.invoke(午))
  }

}
