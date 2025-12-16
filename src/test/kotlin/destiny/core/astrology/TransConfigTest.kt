/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TransConfigTest : AbstractConfigTest<TransConfig>() {

  override val serializer: KSerializer<TransConfig> = TransConfig.serializer()

  override val configByConstructor: TransConfig = TransConfig(discCenter = true,
                                                              refraction = false,
                                                              temperature = 23.0,
                                                              pressure = 1000.0,
                                                              starTypeOptions = StarTypeOptions(NodeType.TRUE, MeanOscu.OSCU))

  override val configByFunction: TransConfig = trans {
    discCenter = true
    refraction = false
    temperature = 23.0
    pressure = 1000.0
    starTypeOptions = StarTypeOptions(NodeType.TRUE, MeanOscu.OSCU)
  }

  override val assertion: (String) -> Unit = { raw ->
    logger.info { raw }
    val actual = Json.decodeFromString<JsonElement>(raw)
    val expected = Json.decodeFromString<JsonElement>("""
      {
        "discCenter": true,
        "refraction": false,
        "temperature": 23.0,
        "pressure": 1000.0,
        "starTypeOptions": {
          "nodeType": "TRUE",
          "apsisType": "OSCU"
        }
      }
    """.trimIndent())
    assertEquals(expected, actual)
  }
}
