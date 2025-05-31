/**
 * Created by smallufo on 2025-05-30.
 */
package destiny.core

import com.jayway.jsonpath.JsonPath
import destiny.core.calendar.locationOf
import destiny.core.electional.Electional
import destiny.core.electional.ElectionalPurpose
import destiny.tools.KotlinLogging
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ElectionalDayHourModelTest {
  private val logger = KotlinLogging.logger { }

  private val json = Json {
    encodeDefaults = false
    prettyPrint = true
    serializersModule = SerializersModule {
      contextual(IBirthDataNamePlaceSerializer)
      polymorphic(Electional.IDayHourModel::class) {
        subclass(Electional.DayHourModel::class)
      }
    }
  }

  @Test
  fun testSerializeDeserialize() {
    val loc = locationOf(Locale.TAIWAN)
    val inner = BirthDataNamePlace(Gender.男, LocalDateTime.of(1990, 1, 1, 12, 0), loc, "小明", "台北市")

    val fromDate = LocalDate.of(2025, 6, 1)
    val toDate = LocalDate.of(2025, 6, 5)
    val traversalModel = Electional.TraversalModel(fromDate, toDate, loc)
    val dayHourModel = Electional.DayHourModel(traversalModel, 3, ElectionalPurpose.DATING, false)

    val model = ElectionalDayHourRequest(inner, dayHourModel)

    json.encodeToString(model).also { rawJson ->
      logger.info { "raw json = $rawJson" }
      assertNotNull(JsonPath.read(rawJson , "$.inner"))
      assertNotNull(JsonPath.read(rawJson , "$.dayHourModel"))
      json.decodeFromString<ElectionalDayHourRequest>(rawJson).also { parsed ->
        assertEquals(parsed, model)
      }
    }
  }
}
