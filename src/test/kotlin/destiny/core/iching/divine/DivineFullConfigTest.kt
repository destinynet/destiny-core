/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.core.iching.divine

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
import kotlin.test.assertTrue

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
    assertTrue(raw.contains(""""eightWordsNullable":\s*"1,0,0,12,3,3,0,0"""".toRegex()))
    assertTrue(raw.contains(""""question":\s*"請問..."""".toRegex()))
    assertTrue(raw.contains(""""approach":\s*"RANDOM"""".toRegex()))
  }

}
