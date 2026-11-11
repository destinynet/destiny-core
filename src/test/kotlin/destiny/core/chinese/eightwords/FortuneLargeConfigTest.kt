/**
 * Created by smallufo on 2021-08-26.
 */
package destiny.core.chinese.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class FortuneLargeConfigTest : AbstractConfigTest<FortuneLargeConfig>() {

  override val serializer: KSerializer<FortuneLargeConfig> = FortuneLargeConfig.serializer()

  override val configByConstructor: FortuneLargeConfig = FortuneLargeConfig(FortuneLargeImpl.SolarTermsSpan, 90.0, listOf(IntAgeNote.Minguo))

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("SolarTermsSpan", JsonPath.read<String>(raw, "$.impl"))
    assertEquals(90.0, JsonPath.read<Double>(raw, "$.span"))
    assertEquals(listOf("Minguo"), JsonPath.read<List<String>>(raw, "$.intAgeNotes"))
  }
}
