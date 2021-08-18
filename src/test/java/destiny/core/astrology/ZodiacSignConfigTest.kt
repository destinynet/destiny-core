/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.ZodiacSignBuilder.Companion.zodiacSign
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class ZodiacSignConfigTest : AbstractConfigTest<ZodiacSignConfig>() {
  override val serializer: KSerializer<ZodiacSignConfig> = ZodiacSignConfig.serializer()

  override val configByConstructor: ZodiacSignConfig = ZodiacSignConfig(Planet.MOON)

  override val configByFunction: ZodiacSignConfig = zodiacSign {
    star = Planet.MOON
  }

  override val assertion: (String) -> Unit = {raw : String ->
    assertTrue(raw.contains(""""star":\s*"Planet.MOON"""".toRegex()))
  }

}
