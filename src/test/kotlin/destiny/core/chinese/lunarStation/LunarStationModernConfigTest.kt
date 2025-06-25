/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.LunarStationConfigBuilder.Companion.lunarStation
import destiny.core.chinese.lunarStation.LunarStationModernConfigBuilder.Companion.lunarStationModern
import kotlinx.serialization.KSerializer
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LunarStationModernConfigTest : AbstractConfigTest<LunarStationModernConfig>() {
  override val serializer: KSerializer<LunarStationModernConfig> = LunarStationModernConfig.serializer()

  override val configByConstructor: LunarStationModernConfig? = null
//    LunarStationModernConfig(
//    method = IModernContextModel.Method.SPECIFIED,
//    specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0)),
//    description = "test123"
//  )

  override val configByFunction: LunarStationModernConfig
    get() {

      val lsConfig = with(EightWordsConfig()) {
        lunarStation {
          discCenter = true
          refraction = false

          yearType = YearType.YEAR_LUNAR
          yearEpoch = YearEpoch.EPOCH_1864

          changeDayAfterZi = false
          monthlyImpl = MonthlyImpl.AnimalExplained
          hourlyImpl = HourlyImpl.Fixed
        }
      }


      return with(lsConfig) {
        lunarStationModern {
          method = IModernContextModel.Method.SPECIFIED
          specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0))
          description = "test123"
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    logger.info { raw }

    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))

    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))
    assertFalse(raw.contains(""""yearEpoch":\s*"EPOCH_1564"""".toRegex()))

    assertTrue(raw.contains(""""method":\s*"SPECIFIED"""".toRegex()))
    assertTrue(raw.contains(""""julDay":\s*2459449.""".toRegex()))
    assertTrue(raw.contains(""""description":\s*"test123"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

  }
}
