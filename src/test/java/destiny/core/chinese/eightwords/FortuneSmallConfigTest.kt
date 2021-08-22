/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeImpl
import destiny.core.IntAgeNoteImpl
import destiny.core.chinese.eightwords.FortuneSmallConfigBuilder.Companion.fortuneSmall
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class FortuneSmallConfigTest : AbstractConfigTest<FortuneSmallConfig>() {

  override val serializer: KSerializer<FortuneSmallConfig> = FortuneSmallConfig.serializer()

  override val configByConstructor: FortuneSmallConfig =
    FortuneSmallConfig(FortuneSmallConfig.Impl.Star, 90, IntAgeImpl.Ziwei, listOf(IntAgeNoteImpl.Minguo))

  override val configByFunction: FortuneSmallConfig = fortuneSmall {
    impl = FortuneSmallConfig.Impl.Star
    count = 90
    intAgeImpl= IntAgeImpl.Ziwei
    intAgeNotes(listOf(IntAgeNoteImpl.Minguo))
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"Star"""".toRegex()))
    assertTrue(raw.contains(""""count":\s*90""".toRegex()))
    assertTrue(raw.contains(""""intAgeImpl":\s*"Ziwei"""".toRegex()))
    assertTrue(raw.contains("""\[\s*"Minguo"\s*]""".toRegex()))
  }
}
