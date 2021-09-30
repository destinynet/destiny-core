/**
 * Created by smallufo on 2021-10-01.
 */
package destiny.core.chinese.holo

import destiny.core.AbstractConfigTest
import destiny.core.chinese.SeasonalSymbolConfig
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan
import destiny.core.chinese.holo.HoloConfigBuilder.Companion.holoConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HoloConfigTest : AbstractConfigTest<HoloConfig>() {

  override val serializer: KSerializer<HoloConfig> = HoloConfig.serializer()

  override val configByConstructor: HoloConfig = HoloConfig(

    seasonalSymbolConfig = SeasonalSymbolConfig(Holo(EndSeasonSymbolSpan.FULL_MONTH)),
    threeKings = ThreeKingsAlgo.MONTH_BRANCH,
    hexChange = HexChange.SRC
  )

  override val configByFunction: HoloConfig = holoConfig {
    seasonalSymbolConfig = SeasonalSymbolConfig(Holo(EndSeasonSymbolSpan.FULL_MONTH))
    threeKings = ThreeKingsAlgo.MONTH_BRANCH
    hexChange = HexChange.SRC
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""type":\s*"Holo"""".toRegex()))
    assertTrue(raw.contains(""""endSeasonSymbolSpan":\s*"FULL_MONTH"""".toRegex()))
    assertTrue(raw.contains(""""threeKings":\s*"MONTH_BRANCH"""".toRegex()))
    assertTrue(raw.contains(""""hexChange":\s*"SRC"""".toRegex()))
  }
}
