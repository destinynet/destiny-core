/**
 * Created by smallufo on 2024-09-01.
 */
package destiny.core.chinese.ziwei

import destiny.core.BirthDataNamePlace
import destiny.core.Gender
import destiny.core.calendar.locationOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class ZiweiDivineTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testSerialization() {
    val loc = locationOf(Locale.TAIWAN)
    val ziweiDivine = ZiweiDivine(
      BirthDataNamePlace(Gender.男, LocalDateTime.of(2024, 9, 1, 23, 55), loc, "name", "Taipei"),
      "Question"
    )
    Json.encodeToString(ziweiDivine).also {
      logger.info { it }
    }
  }
}
