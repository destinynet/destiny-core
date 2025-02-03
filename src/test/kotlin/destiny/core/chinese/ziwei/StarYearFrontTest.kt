/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei

import destiny.core.AbstractPointTest
import destiny.core.chinese.Branch.*
import destiny.core.getAbbreviation
import destiny.core.toString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class StarYearFrontTest : AbstractPointTest(StarYearFront::class) {

  @Test
  fun testSerialize() {
    StarYearFront.values.forEach { s ->
      val rawJson = Json.encodeToString(s)
      logger.info { "$s = $rawJson" }
      assertSame(s, decodeFromString(rawJson))
    }
  }

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
