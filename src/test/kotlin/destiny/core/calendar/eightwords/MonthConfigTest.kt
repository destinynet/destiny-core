/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.calendar.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.MonthConfigBuilder.Companion.monthConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals


class MonthConfigTest : AbstractConfigTest<MonthConfig>() {
  override val serializer: KSerializer<MonthConfig> = MonthConfig.serializer()

  override val configByConstructor: MonthConfig = MonthConfig(
    southernHemisphereOpposition = true,
    hemisphereBy = HemisphereBy.DECLINATION,
    monthImpl = MonthImpl.SunSign
  )

  override val configByFunction: MonthConfig = monthConfig {
    southernHemisphereOpposition = true
    hemisphereBy = HemisphereBy.DECLINATION
    monthImpl = MonthImpl.SunSign
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.southernHemisphereOpposition"))
    assertEquals("DECLINATION", JsonPath.read<String>(raw, "$.hemisphereBy"))
    assertEquals("SunSign", JsonPath.read<String>(raw, "$.monthImpl"))
  }
}
