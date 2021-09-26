/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import destiny.core.AbstractConfigTest
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YangBlade
import destiny.core.iching.Hexagram
import destiny.core.iching.divine.DivineTraditionalConfigBuilder.Companion.divineTraditionalConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DivineTraditionalConfigTest : AbstractConfigTest<DivineTraditionalConfig>() {

  override val serializer: KSerializer<DivineTraditionalConfig> = DivineTraditionalConfig.serializer()

  override val configByConstructor: DivineTraditionalConfig = DivineTraditionalConfig(
    Hexagram.既濟, Hexagram.未濟,
    SettingsOfStemBranch.TsangShan, HiddenEnergy.GingFang, Tianyi.ZiweiBook, YangBlade.RobCash
  )

  override val configByFunction: DivineTraditionalConfig = divineTraditionalConfig {
    src = Hexagram.既濟
    dst = Hexagram.未濟
    settingsOfStemBranch = SettingsOfStemBranch.TsangShan
    hiddenEnergy = HiddenEnergy.GingFang
    tianyi = Tianyi.ZiweiBook
    yangBlade = YangBlade.RobCash
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""src":\s*"101010"""".toRegex()))
    assertTrue(raw.contains(""""dst":\s*"010101"""".toRegex()))
    assertTrue(raw.contains(""""settingsOfStemBranch":\s*"TsangShan"""".toRegex()))
    assertTrue(raw.contains(""""hiddenEnergy":\s*"GingFang"""".toRegex()))
    assertTrue(raw.contains(""""tianyi":\s*"ZiweiBook"""".toRegex()))
    assertTrue(raw.contains(""""yangBlade":\s*"RobCash"""".toRegex()))
  }
}
