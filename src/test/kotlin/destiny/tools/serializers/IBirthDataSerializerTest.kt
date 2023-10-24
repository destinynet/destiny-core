/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.BirthData
import destiny.core.Gender
import destiny.core.ITimeLoc
import destiny.core.TimeLoc
import destiny.core.calendar.locationOf
import destiny.tools.serializers.Assertions.assertBirthDataEquals
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class IBirthDataSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
    encodeDefaults = false
  }

  @Test
  fun testSerializeDeserialize() {
    val timeLoc: ITimeLoc = TimeLoc(LocalDateTime.of(2023, 10, 24, 21, 40), locationOf(Locale.TAIWAN))
    val gender = Gender.女

    val birthData = BirthData(timeLoc, gender)

    val serialized = json.encodeToString(IBirthDataSerializer, birthData)
    logger.info { serialized }
    val deserialized = json.decodeFromString(IBirthDataSerializer, serialized)

    assertBirthDataEquals(birthData, deserialized)
  }
}
