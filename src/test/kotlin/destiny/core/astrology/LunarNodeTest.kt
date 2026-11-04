/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.astrology

import destiny.core.News
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.*


// TODO : AbstractPointTest
class LunarNodeTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testSerialize() {
    LunarNode.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      logger.info { "$p = $rawJson" }
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }

  @Test
  fun testCompare() {
    LunarNode.values.toList().shuffled().sorted().zip(LunarNode.values).forEach { (p1, p2) ->
      assertSame(p1, p2)
    }
  }

  @Test
  fun testToStringLocale() {
    assertEquals("北交點" , LunarNode.NORTH.getTitle(Locale.TAIWAN))
    assertEquals("北交点" , LunarNode.NORTH.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("North" , LunarNode.NORTH.getTitle(Locale.ENGLISH))

    for (each in LunarNode.values) {
      assertNotNull(each.getTitle(Locale.TAIWAN))
      assertNotNull(each.getTitle(Locale.SIMPLIFIED_CHINESE))
      assertNotNull(each.getTitle(Locale.ENGLISH))
      logger.info("{}", each.toString())
    }


    val set = LunarNode.values.map { it.getTitle(Locale.TAIWAN) }.toSet()
    assertTrue(set.contains("北交點"))
    assertTrue(set.contains("南交點"))
  }

  @Test
  fun testEquals() {
    assertSame(LunarNode.NORTH, LunarNode.of(News.NorthSouth.NORTH))
    assertSame(LunarNode.SOUTH, LunarNode.of(News.NorthSouth.SOUTH))
  }

  @Test
  fun testStringConvert() {
    LunarNode.values.forEach { star ->
      logger.info { "$star = ${star.getTitle(Locale.ENGLISH)}" }
      assertSame(star, LunarNode.fromString(star.getTitle(Locale.ENGLISH)))
    }
  }
}
