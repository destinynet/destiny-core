/**
 * Created by smallufo on 2024-07-20.
 */
package destiny.tools.ai.model

import destiny.core.EnumTest
import destiny.tools.parseJsonToMap
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BirthDataDomainTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(BirthDataDomain::class)
  }


  @Nested
  inner class ParseStringToMap {

    @Test
    fun invalid() {
      val json = """
        invalid json
      """.trimIndent()

      val domainMap = parseJsonToMap<BirthDataDomain>(json)
      assertTrue(domainMap.isEmpty())
    }

    @Test
    fun complete() {
      val json = """
      {
        "GENERAL": "Describe the person's main destiny",
        "LOVE": "Love fortune",
        "CAREER": "Career and job",
        "MONEY": "Financial fortune",
        "STUDY": "Study fortune",
        "FAMILY": "Family fortune",
        "HEALTH": "Physical health and precautions",
        "MARRIAGE": "Marriage relationships",
        "FRIEND": "Friendship fortune",
        "PERSONALITY": "Personality traits",
        "VISION": "Vision, travel, religious beliefs",
        "SECRET": "Secrets, hidden issues"
      }
    """

      val domainMap = parseJsonToMap<BirthDataDomain>(json)
      assertEquals(BirthDataDomain.entries.size, domainMap.size)
    }

    @Test
    fun incomplete() {
      val json = """
      {
        "GENERAL": "Describe the person's main destiny",
        "SECRET": "Secrets, hidden issues"
      }
    """

      val domainMap = parseJsonToMap<BirthDataDomain>(json)
      assertEquals(2, domainMap.size)
    }

    @Test
    fun contaminated() {
      val json = """
      {
        "GENERAL": "Describe the person's main destiny",
        "XXX": "XXX",
        "SECRET": "Secrets, hidden issues"
      }
    """

      val domainMap = parseJsonToMap<BirthDataDomain>(json)
      assertEquals(2, domainMap.size)
    }
  }


}
