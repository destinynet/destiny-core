/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StarYearFrontTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
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