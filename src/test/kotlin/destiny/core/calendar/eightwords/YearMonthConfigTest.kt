/**
 * Created by smallufo on 2021-08-09.
 */
package destiny.core.calendar.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class YearMonthConfigTest : AbstractConfigTest<YearMonthConfig>() {

  override val serializer: KSerializer<YearMonthConfig> = YearMonthConfig.serializer()

  override val configByConstructor = YearMonthConfig(
    YearConfig(270.0),
    MonthConfig(
      southernHemisphereOpposition = true,
      hemisphereBy = HemisphereBy.DECLINATION,
      monthImpl = MonthImpl.SunSign
    )
  )

  override val assertion = { raw: String ->
    assertEquals(270.0, JsonPath.read<Double>(raw, "$.yearConfig.changeYearDegree"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.monthConfig.southernHemisphereOpposition"))
    assertEquals("DECLINATION", JsonPath.read<String>(raw, "$.monthConfig.hemisphereBy"))
    assertEquals("SunSign", JsonPath.read<String>(raw, "$.monthConfig.monthImpl"))
  }

}
