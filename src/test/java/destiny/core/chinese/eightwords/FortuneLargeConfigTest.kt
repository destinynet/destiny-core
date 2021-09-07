/**
 * Created by smallufo on 2021-08-26.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNoteImpl
import destiny.core.chinese.eightwords.FortuneLargeConfigBuilder.Companion.fortuneLarge
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class FortuneLargeConfigTest : AbstractConfigTest<FortuneLargeConfig>() {

  override val serializer: KSerializer<FortuneLargeConfig> = FortuneLargeConfig.serializer()

  override val configByConstructor: FortuneLargeConfig = FortuneLargeConfig(FortuneLargeConfig.Impl.SolarTermsSpan, 90.0, listOf(IntAgeNoteImpl.Minguo))

  override val configByFunction: FortuneLargeConfig = fortuneLarge {
    impl = FortuneLargeConfig.Impl.SolarTermsSpan
    span = 90.0
    intAgeNotes(listOf(IntAgeNoteImpl.Minguo))
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"SolarTermsSpan"""".toRegex()))
    assertTrue(raw.contains(""""span":\s*90.0""".toRegex()))
    assertTrue(raw.contains(""""intAgeNotes":\s*\[\s*"Minguo"\s*]""".toRegex()))
  }
}
