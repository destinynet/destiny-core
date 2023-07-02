/**
 * Created by smallufo on 2021-08-26.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.eightwords.FortuneLargeConfigBuilder.Companion.fortuneLarge
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class FortuneLargeConfigTest : AbstractConfigTest<FortuneLargeConfig>() {

  override val serializer: KSerializer<FortuneLargeConfig> = FortuneLargeConfig.serializer()

  override val configByConstructor: FortuneLargeConfig = FortuneLargeConfig(FortuneLargeImpl.SolarTermsSpan, 90.0, listOf(IntAgeNote.Minguo))

  override val configByFunction: FortuneLargeConfig
    get() {
      return with(EightWordsConfig()) {
        fortuneLarge {
          impl = FortuneLargeImpl.SolarTermsSpan
          span = 90.0
          intAgeNotes(listOf(IntAgeNote.Minguo))
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"SolarTermsSpan"""".toRegex()))
    assertTrue(raw.contains(""""span":\s*90.0""".toRegex()))
    assertTrue(raw.contains(""""intAgeNotes":\s*\[\s*"Minguo"\s*]""".toRegex()))
  }
}
