/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.ZodiacSignBuilder.Companion.zodiacSign
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ZodiacSignConfigTest : AbstractConfigTest<ZodiacSignConfig>() {
  override val serializer: KSerializer<ZodiacSignConfig> = ZodiacSignConfig.serializer()

  override val configByConstructor: ZodiacSignConfig = ZodiacSignConfig(LunarNode.NORTH, StarTypeOptions.PRECISE)

  override val configByFunction: ZodiacSignConfig = zodiacSign {
    star = LunarNode.NORTH
    starTypeOptions = StarTypeOptions.PRECISE
  }

  override val assertion: (String) -> Unit = {raw : String ->
    val actual = Json.decodeFromString<JsonElement>(raw)
    val expected = Json.decodeFromString<JsonElement>("""
      {
        "star": "LunarNode.NORTH",
        "starTypeOptions": {
          "nodeType": "TRUE",
          "apsisType": "OSCU"
        }
      }
    """.trimIndent())
    assertEquals(expected, actual)
  }

}
