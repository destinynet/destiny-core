/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import destiny.core.calendar.eightwords.DayConfigBuilder.Companion.dayConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour
import destiny.core.calendar.eightwords.HourBranchConfigBuilder.Companion.hourBranchConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class DayHourConfigTest : AbstractConfigTest<DayHourConfig>() {

  override val serializer: KSerializer<DayHourConfig> = DayHourConfig.serializer()

  override val configByConstructor: DayHourConfig = DayHourConfig(
    DayConfig(changeDay = ChangeDay.ZI_MIDDLE, midnight = MidnightImpl.CLOCK0),
    HourBranchConfig(
      hourImpl = HourImpl.LMT,
      transConfig = TransConfig(
        discCenter = true,
        refraction = false,
        temperature = 23.0,
        pressure = 1000.0
      )
    )
  )

  override val configByFunction: DayHourConfig
    get() {

      val transConfig = trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      val hourBranchConfig = with(transConfig) {
        hourBranchConfig {
          hourImpl = HourImpl.LMT
        }
      }

      val dayConfig = dayConfig {
        changeDay = ChangeDay.ZI_MIDDLE
        midnight = MidnightImpl.CLOCK0
      }

      return with(dayConfig) {
        with(hourBranchConfig) {
          dayHour {

          }
        }
      }

    }

  override val assertion = { raw: String ->
    assertEquals("ZI_MIDDLE", JsonPath.read<String>(raw, "$.dayConfig.changeDay"))
    assertEquals("CLOCK0", JsonPath.read<String>(raw, "$.dayConfig.midnight"))
    assertEquals("LMT", JsonPath.read<String>(raw, "$.hourBranchConfig.hourImpl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.hourBranchConfig.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.hourBranchConfig.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.hourBranchConfig.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.hourBranchConfig.transConfig.pressure"))
  }
}
