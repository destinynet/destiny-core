/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.core.chinese

import destiny.core.AbstractConfigTest
import destiny.core.chinese.SeasonalSymbolConfigBuilder.Companion.seasonalSymbolConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class SeasonalSymbolConfigTest : AbstractConfigTest<SeasonalSymbolConfig>() {
  override val serializer: KSerializer<SeasonalSymbolConfig> = SeasonalSymbolConfig.serializer()

  override val configByConstructor: SeasonalSymbolConfig =
    SeasonalSymbolConfig(
      impl = SeasonalSymbolConfig.Impl.Holo(SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.FULL_MONTH)
    )

  override val configByFunction: SeasonalSymbolConfig = seasonalSymbolConfig {
    impl = SeasonalSymbolConfig.Impl.Holo(SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.FULL_MONTH)
  }

  override val assertion: (String) -> Unit = { raw: String ->

    assertTrue(raw.contains(""""type":\s*"Holo"""".toRegex()))
    assertTrue(raw.contains(""""endSeasonSymbolSpan":\s*"FULL_MONTH"""".toRegex()))
  }
}
