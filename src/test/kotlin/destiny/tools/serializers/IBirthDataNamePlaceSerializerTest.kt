/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.*
import destiny.core.calendar.locationOf
import destiny.tools.serializers.Assertions.assertBdnpEquals
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class IBirthDataNamePlaceSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
    encodeDefaults = false
  }

  @Test
  fun testSerializeDeserialize() {
    val timeLoc: ITimeLoc = TimeLoc(LocalDateTime.of(2023, 10, 24, 21, 40), locationOf(Locale.TAIWAN))
    val gender = Gender.女
    val birthData = BirthData(timeLoc, gender)
    val name = "許功蓋"
    val place = "台北市"
    val bdnp = BirthDataNamePlace(birthData, name, place)

    val serialized = json.encodeToString(IBirthDataNamePlaceSerializer, bdnp)
    logger.info { serialized }
    val deserialized = json.decodeFromString(IBirthDataNamePlaceSerializer, serialized)

    assertBdnpEquals(bdnp, deserialized)
  }

  @Test
  fun testSerializeDeserializeWithNullFields() {
    val timeLoc: ITimeLoc = TimeLoc(LocalDateTime.of(2023, 10, 24, 21, 40), locationOf(Locale.TAIWAN))
    val gender = Gender.女
    val birthData = BirthData(timeLoc, gender)
    val name = null
    val place = null
    val bdnp = BirthDataNamePlace(birthData, name, place)

    val serialized = json.encodeToString(IBirthDataNamePlaceSerializer, bdnp)
    logger.info { serialized }
    val deserialized = json.decodeFromString(IBirthDataNamePlaceSerializer, serialized)

    assertBdnpEquals(bdnp, deserialized)
  }
}
