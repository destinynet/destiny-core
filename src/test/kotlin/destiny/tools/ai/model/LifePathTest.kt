/**
 * Created by smallufo on 2024-07-26.
 */
package destiny.tools.ai.model

import destiny.tools.KotlinLogging
import destiny.tools.getTitle
import destiny.tools.parseJsonToMap
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LifePathTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testEnum() {
    LifePath.entries.forEach { domain ->
      logger.info { "$domain : ${domain.getTitle(Locale.TAIWAN)}" }
      assertNotEquals(domain.name, domain.getTitle(Locale.TAIWAN))
    }
  }

  @Nested
  inner class ParseStringToMap {

    @Test
    fun invalid() {
      val json = """
        invalid json
      """.trimIndent()

      val map = parseJsonToMap<LifePath>(json)
      assertTrue { map.isEmpty() }
    }

    @Test
    fun complete() {
      val json = """
        {
          "GENERAL" : "請描運勢總覽",
          "LOVE" : "請描述感情運勢",
          "ACCIDENT" : "是否有意外傷災要注意？",
          "EXAM" : "考試運勢如何？",
          "NEGOTIATION" : "交涉、談判運勢如何？",
          "CAREER" : "職場、工作運勢如何？",
          "FINANCIAL" : "財運、投資運勢如何？",
          "MARRIAGE" : "婚姻如何？",
          "HEALTH" : "健康如何？",
          "FRIEND" : "交友運如何？",
          "FAMILY" : "家庭關係如何？"
        }
      """.trimIndent()
      val map = parseJsonToMap<LifePath>(json)
      assertEquals(LifePath.entries.size, map.size)
    }

    @Test
    fun incomplete() {
      val json = """
        {
          "GENERAL" : "請描運勢總覽",
          "FAMILY" : "家庭關係如何？"
        }
      """.trimIndent()
      val map = parseJsonToMap<LifePath>(json)
      assertEquals(2, map.size)
    }

    @Test
    fun contaminated() {
      val json = """
        {
          "GENERAL" : "請描運勢總覽",
          "XXX" : "XXX",
          "FAMILY" : "家庭關係如何？"
        }
      """.trimIndent()
      val map = parseJsonToMap<LifePath>(json)
      assertEquals(2, map.size)
    }
  }
}
