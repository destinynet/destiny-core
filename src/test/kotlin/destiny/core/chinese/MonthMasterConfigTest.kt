/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.chinese

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.HemisphereBy
import destiny.core.calendar.eightwords.MonthConfig
import destiny.core.calendar.eightwords.MonthImpl
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class MonthMasterConfigTest : AbstractConfigTest<MonthMasterConfig>() {

  override val serializer: KSerializer<MonthMasterConfig> = MonthMasterConfig.serializer()

  override val configByConstructor: MonthMasterConfig = MonthMasterConfig(
    MonthMasterConfig.Impl.Combined, MonthConfig(true, HemisphereBy.DECLINATION, MonthImpl.SunSign)
  )

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Combined", JsonPath.read<String>(raw, "$.impl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.monthConfig.southernHemisphereOpposition"))
    assertEquals("DECLINATION", JsonPath.read<String>(raw, "$.monthConfig.hemisphereBy"))
    assertEquals("SunSign", JsonPath.read<String>(raw, "$.monthConfig.monthImpl"))
  }
}
