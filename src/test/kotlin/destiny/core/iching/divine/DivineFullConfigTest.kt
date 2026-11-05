/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.core.iching.divine

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.chinese.Branch.亥
import destiny.core.chinese.Branch.寅
import destiny.core.chinese.Stem.丙
import destiny.core.chinese.Stem.甲
import destiny.core.chinese.StemBranchOptional
import destiny.core.iching.divine.DivineFullConfigBuilder.Companion.divineFullConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class DivineFullConfigTest : AbstractConfigTest<DivineFullConfig>() {

  override val serializer: KSerializer<DivineFullConfig> = DivineFullConfig.serializer()

  override val configByConstructor: DivineFullConfig = DivineFullConfig(
    eightWordsNullable = EightWordsNullable.of(
      StemBranchOptional(甲, null),
      StemBranchOptional(null, 亥),
      StemBranchOptional(丙, 寅),
      StemBranchOptional(null, null)
    ),
    question = "請問...",
    approach = DivineApproach.RANDOM
  )

  override val configByFunction: DivineFullConfig
    get() {
      return with(EightWordsConfig()) {
        divineFullConfig {
          eightWordsNullable = EightWordsNullable.of(
            StemBranchOptional(甲, null),
            StemBranchOptional(null, 亥),
            StemBranchOptional(丙, 寅),
            StemBranchOptional(null, null)
          )
          question = "請問..."
          approach = DivineApproach.RANDOM
        }
      }
    }

  override val assertion: (String) -> Unit = {raw : String ->
    assertEquals("1,0,0,12,3,3,0,0", JsonPath.read<String>(raw, "$.eightWordsNullable"))
    assertEquals("請問...", JsonPath.read<String>(raw, "$.question"))
    assertEquals("RANDOM", JsonPath.read<String>(raw, "$.approach"))
  }

}
