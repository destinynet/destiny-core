/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YangBlade
import destiny.core.iching.Hexagram
import destiny.core.iching.divine.DivineTraditionalConfigBuilder.Companion.divineTraditionalConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class DivineTraditionalConfigTest : AbstractConfigTest<DivineTraditionalConfig>() {

  override val serializer: KSerializer<DivineTraditionalConfig> = DivineTraditionalConfig.serializer()

  override val configByConstructor: DivineTraditionalConfig = DivineTraditionalConfig(
    Hexagram.既濟, Hexagram.未濟,
    SettingsOfStemBranch.TsangShan, HiddenEnergy.GingFang, Tianyi.ZiweiBook, YangBlade.RobCash
  )

  override val configByFunction: DivineTraditionalConfig
    get() {
      return with(EightWordsConfig()) {
        divineTraditionalConfig {
          src = Hexagram.既濟
          dst = Hexagram.未濟
          settings = SettingsOfStemBranch.TsangShan
          hiddenEnergy = HiddenEnergy.GingFang
          tianyi = Tianyi.ZiweiBook
          yangBlade = YangBlade.RobCash
        }
      }
    }

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals("101010", JsonPath.read<String>(raw, "$.src"))
    assertEquals("010101", JsonPath.read<String>(raw, "$.dst"))
    assertEquals("TsangShan", JsonPath.read<String>(raw, "$.settings"))
    assertEquals("GingFang", JsonPath.read<String>(raw, "$.hiddenEnergy"))
    assertEquals("ZiweiBook", JsonPath.read<String>(raw, "$.tianyi"))
    assertEquals("RobCash", JsonPath.read<String>(raw, "$.yangBlade"))
  }
}
