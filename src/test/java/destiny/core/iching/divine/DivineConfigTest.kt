/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import destiny.core.AbstractConfigTest
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YangBlade
import destiny.core.iching.divine.DivineConfigBuilder.Companion.divineConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DivineConfigTest : AbstractConfigTest<DivineConfig>() {

  override val serializer: KSerializer<DivineConfig> = DivineConfig.serializer()

  override val configByConstructor: DivineConfig = DivineConfig(
    SettingsOfStemBranch.TsangShan, HiddenEnergy.GingFang, Tianyi.ZiweiBook, YangBlade.RobCash
  )

  override val configByFunction: DivineConfig = divineConfig {
    settingsOfStemBranch = SettingsOfStemBranch.TsangShan
    hiddenEnergy = HiddenEnergy.GingFang
    tianyi = Tianyi.ZiweiBook
    yangBlade = YangBlade.RobCash
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""settingsOfStemBranch":\s*"TsangShan"""".toRegex()))
    assertTrue(raw.contains(""""hiddenEnergy":\s*"GingFang"""".toRegex()))
    assertTrue(raw.contains(""""tianyi":\s*"ZiweiBook"""".toRegex()))
    assertTrue(raw.contains(""""yangBlade":\s*"RobCash"""".toRegex()))
  }
}
