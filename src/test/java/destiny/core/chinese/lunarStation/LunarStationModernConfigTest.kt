/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.TimeTools
import destiny.core.chinese.lunarStation.LunarStationModernConfigBuilder.Companion.lunarStationModern
import kotlinx.serialization.KSerializer
import java.time.LocalDateTime
import kotlin.test.assertTrue

internal class LunarStationModernConfigTest : AbstractConfigTest<LunarStationModernConfig>() {
  override val serializer: KSerializer<LunarStationModernConfig> = LunarStationModernConfig.serializer()

  override val configByConstructor: LunarStationModernConfig = LunarStationModernConfig(
    method = IModernContextModel.Method.SPECIFIED,
    specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0)),
    description = "test123"
  )

  override val configByFunction: LunarStationModernConfig = lunarStationModern {
    method = IModernContextModel.Method.SPECIFIED
    specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0))
    description = "test123"
  }

  override val assertion: (String) -> Unit = { raw ->
    logger.info { raw }
    assertTrue(raw.contains(""""method":\s*"SPECIFIED"""".toRegex()))
    assertTrue(raw.contains(""""specifiedGmtJulDay":\s*2459449\.0""".toRegex()))
    assertTrue(raw.contains(""""description":\s*"test123"""".toRegex()))
  }
}
