/**
 * Created by smallufo on 2024-10-30.
 */
package destiny.core.astrology

import destiny.core.*
import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import destiny.tools.ai.model.Domain
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SynastryModelTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
    encodeDefaults = false
    serializersModule = SerializersModule {
      contextual(IBirthDataNamePlaceSerializer)
    }
  }

  @Test
  fun testSerializeDeserialize() {
    val loc = locationOf(Locale.TAIWAN)
    val inner = BirthDataNamePlace(Gender.男, LocalDateTime.of(1990, 1, 1, 12, 0), loc, "小明", "台北市")
    val outer = BirthDataNamePlace(Gender.女, LocalDateTime.of(1994, 7, 1, 12, 0), loc, "小花", "台北市")

    val request = SynastryModel(inner, outer, SynastryGrain.INNER_FULL_OUTER_DATE, Domain.Bdnp.HOROSCOPE, SynastryAspect.LOVE)

    json.encodeToString(request).also { rawJson ->
      logger.info { "raw json = $rawJson" }
      json.decodeFromString<SynastryModel>(rawJson).also { deserialized ->
        assertEquals(request , deserialized)
      }
    }
  }

}
