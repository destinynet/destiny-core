/**
 * Created by smallufo on 2021-10-01.
 */
package destiny.core.chinese.holo

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.chinese.SeasonalSymbolConfig
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo
import destiny.core.chinese.SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan
import destiny.core.chinese.holo.HoloConfigBuilder.Companion.holoConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class HoloConfigTest : AbstractConfigTest<HoloConfig>() {

  override val serializer: KSerializer<HoloConfig> = HoloConfig.serializer()

  override val configByConstructor: HoloConfig = HoloConfig(

    seasonalSymbolConfig = SeasonalSymbolConfig(Holo(EndSeasonSymbolSpan.FULL_MONTH)),
    threeKings = ThreeKingsAlgo.MONTH_BRANCH,
    hexChange = HexChange.SRC,
    monthlyHexagram = MonthlyHexagram.Branch
  )

  override val configByFunction: HoloConfig = holoConfig {
    seasonalSymbolConfig = SeasonalSymbolConfig(Holo(EndSeasonSymbolSpan.FULL_MONTH))
    threeKings = ThreeKingsAlgo.MONTH_BRANCH
    hexChange = HexChange.SRC
    monthlyHexagram = MonthlyHexagram.Branch
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals("Holo", JsonPath.read<String>(raw, "$.seasonalSymbolConfig.impl.type"))
    assertEquals("FULL_MONTH", JsonPath.read<String>(raw, "$.seasonalSymbolConfig.impl.endSeasonSymbolSpan"))
    assertEquals("MONTH_BRANCH", JsonPath.read<String>(raw, "$.threeKings"))
    assertEquals("SRC", JsonPath.read<String>(raw, "$.hexChange"))
    assertEquals("Branch", JsonPath.read<String>(raw, "$.monthlyHexagram"))
  }
}
