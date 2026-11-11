/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.core.chinese

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class SeasonalSymbolConfigTest : AbstractConfigTest<SeasonalSymbolConfig>() {
  override val serializer: KSerializer<SeasonalSymbolConfig> = SeasonalSymbolConfig.serializer()

  override val configByConstructor: SeasonalSymbolConfig =
    SeasonalSymbolConfig(
      impl = SeasonalSymbolConfig.Impl.Holo(SeasonalSymbolConfig.Impl.Holo.EndSeasonSymbolSpan.FULL_MONTH)
    )

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals("Holo", JsonPath.read<String>(raw, "$.impl.type"))
    assertEquals("FULL_MONTH", JsonPath.read<String>(raw, "$.impl.endSeasonSymbolSpan"))
  }
}
