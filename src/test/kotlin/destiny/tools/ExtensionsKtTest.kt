/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsKtTest {

  private val logger = KotlinLogging.logger {}

  @Test
  fun firstNotNullResult_iterable() {
    listOf(null, 'A', 'B').also { list ->
      assertEquals('A', list.firstNotNullResult { it })
      assertEquals('a', list.firstNotNullResult { it?.lowercaseChar() })
    }

    listOf<Char?>(null, null).also { list ->
      assertNull(list.firstNotNullResult { it })
      assertNull(list.firstNotNullResult { it?.lowercaseChar() })
    }
  }

  @Test
  fun firstNotNullResult_sequence() {
    sequenceOf(null, 'A', 'B').also { seq ->
      assertEquals('A', seq.firstNotNullResult { it })
      assertEquals('a', seq.firstNotNullResult { it?.lowercaseChar() })
    }

    sequenceOf<String?>(null, null).also { seq ->
      assertNull(seq.firstNotNullResult { it })
      assertNull(seq.map { it }.firstNotNullResult { it?.lowercase(Locale.getDefault()) })
    }
  }

  @Nested
  inner class TzidTest {

    @Test
    fun nestedObj() {
      val raw = """
        {"birthYear": 1988, "birthMonth": 4, "birthDay": 23, "birthHour": 13, "birthMinute": 55, "birthLat": 24.1418685, "birthLng": 120.6755461, 
        "tzid": {"timeZoneId": "Asia/Taipei"}, 
        "gender": "M", "forecastYear": 2025, "question": "2025年的運勢如何？"}
      """.trimIndent()

      assertEquals("Asia/Taipei", Json.parseToJsonElement(raw).toMap()["tzid"])
    }

  }
}
