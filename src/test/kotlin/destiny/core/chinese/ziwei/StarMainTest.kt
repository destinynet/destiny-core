/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.AbstractPointTest
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.getAbbreviation
import destiny.core.toString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class StarMainTest : AbstractPointTest(StarMain::class) {

  @Test
  fun testSerialize() {
    StarMain.values.forEach { s ->
      val rawJson = Json.encodeToString(s)
      logger.info { "$s = $rawJson" }
      assertSame(s, decodeFromString(rawJson))
    }
  }

  @Test
  fun testCompare() {
    assertEquals(listOf(紫微, 天機), sortedSetOf(紫微, 天機).toList())
    assertEquals(listOf(紫微, 天機), sortedSetOf(天機, 紫微).toList())
  }

  @Test
  fun testToString() {
    assertEquals("天機", 天機.toString(Locale.TAIWAN))
    assertEquals("天机", 天機.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("天機", 天機.toString(Locale.ENGLISH))

    assertEquals("太陰", 太陰.toString(Locale.TAIWAN))
    assertEquals("太阴", 太陰.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("太陰", 太陰.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("機", 天機.getAbbreviation(Locale.TAIWAN))
    assertEquals("机", 天機.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("機", 天機.getAbbreviation(Locale.ENGLISH))

    assertEquals("陰", 太陰.getAbbreviation(Locale.TAIWAN))
    assertEquals("阴", 太陰.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("陰", 太陰.getAbbreviation(Locale.ENGLISH))
  }

}
