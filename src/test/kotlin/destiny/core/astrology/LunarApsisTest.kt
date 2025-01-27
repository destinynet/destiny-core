/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.astrology

import destiny.core.toString
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.*

// TODO : AbstractPointTest
class LunarApsisTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testSerialize() {
    LunarApsis.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      logger.info { "$p = $rawJson" }
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }


  @Test
  fun testCompare() {
    LunarApsis.values.toList().shuffled().sorted().zip(LunarApsis.values).forEach { (p1, p2) ->
      assertSame(p1, p2)
    }
  }

  @Test
  fun testToStringLocale() {

    for (each in LunarApsis.values) {
      assertNotNull(each)
      assertNotNull(each.toString())
      logger.info("{}", each.toString())
    }

    val set = LunarApsis.values.map { it.toString(Locale.TAIWAN) }.toSet()
    assertTrue(set.contains("遠地點"))
    assertTrue(set.contains("近地點"))
  }

  @Test
  fun testStringConvert() {
    LunarApsis.values.forEach { star ->
      logger.info { "$star = ${star::class.simpleName}" }
      assertSame(star, LunarApsis.fromString(star::class.simpleName!!))
    }
  }

}
