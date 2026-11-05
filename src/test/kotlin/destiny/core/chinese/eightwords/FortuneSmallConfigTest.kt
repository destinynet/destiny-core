/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.eightwords.FortuneSmallConfigBuilder.Companion.fortuneSmall
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class FortuneSmallConfigTest : AbstractConfigTest<FortuneSmallConfig>() {

  override val serializer: KSerializer<FortuneSmallConfig> = FortuneSmallConfig.serializer()

  override val configByConstructor: FortuneSmallConfig =
    FortuneSmallConfig(FortuneSmallConfig.Impl.Star, 90, listOf(IntAgeNote.Minguo))

  override val configByFunction: FortuneSmallConfig
    get() {
      return with(EightWordsConfig()) {
        fortuneSmall {
          impl = FortuneSmallConfig.Impl.Star
          count = 90
          intAgeNotes(listOf(IntAgeNote.Minguo))
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Star", JsonPath.read<String>(raw, "$.impl"))
    assertEquals(90, JsonPath.read<Int>(raw, "$.count"))
    assertEquals(listOf("Minguo"), JsonPath.read<List<String>>(raw, "$.intAgeNotes"))
  }
}
