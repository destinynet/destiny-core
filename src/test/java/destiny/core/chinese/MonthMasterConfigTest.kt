/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.chinese

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.HemisphereBy
import destiny.core.calendar.eightwords.MonthConfig
import destiny.core.chinese.MonthMasterConfigBuilder.Companion.monthMaster
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class MonthMasterConfigTest : AbstractConfigTest<MonthMasterConfig>() {

  override val serializer: KSerializer<MonthMasterConfig> = MonthMasterConfig.serializer()

  override val configByConstructor: MonthMasterConfig = MonthMasterConfig(
    MonthMasterConfig.Impl.Combined, MonthConfig(true, HemisphereBy.DECLINATION, MonthConfig.MoonImpl.SunSign)
  )

  override val configByFunction: MonthMasterConfig = monthMaster {
    impl = MonthMasterConfig.Impl.Combined

    month {
      southernHemisphereOpposition = true
      hemisphereBy = HemisphereBy.DECLINATION
      monthImpl = MonthConfig.MoonImpl.SunSign
    }
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"Combined"""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""moonImpl":\s*"SunSign"""".toRegex()))
  }
}
