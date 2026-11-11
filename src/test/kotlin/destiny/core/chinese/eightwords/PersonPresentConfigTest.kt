/**
 * Created by smallufo on 2021-08-28.
 */
package destiny.core.chinese.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.eightwords.*
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class PersonPresentConfigTest : AbstractConfigTest<PersonPresentConfig>() {

  override val serializer: KSerializer<PersonPresentConfig> = PersonPresentConfig.serializer()

  override val configByConstructor: PersonPresentConfig = PersonPresentConfig(
    personContextConfig = EightWordsPersonConfig(
      EightWordsContextConfig(
        EightWordsConfig(
          YearMonthConfig(
            YearConfig(270.0),
            MonthConfig(true, HemisphereBy.DECLINATION, MonthImpl.SunSign)
          )
        )
      )
    ),
    viewGmt = GmtJulDay(0.0)
  )

  override val assertion: (String) -> Unit = { raw ->
    // 三份 eightWordsConfig（命盤、大運、小運）都要是同一組設定
    listOf(
      "$.personContextConfig.eightwordsContextConfig.eightWordsConfig",
      "$.personContextConfig.fortuneLargeConfig.eightWordsConfig",
      "$.personContextConfig.fortuneSmallConfig.eightWordsConfig",
    ).forEach { ew ->
      assertEquals(270.0, JsonPath.read<Double>(raw, "$ew.yearMonthConfig.yearConfig.changeYearDegree"))
      assertEquals(true, JsonPath.read<Boolean>(raw, "$ew.yearMonthConfig.monthConfig.southernHemisphereOpposition"))
      assertEquals("DECLINATION", JsonPath.read<String>(raw, "$ew.yearMonthConfig.monthConfig.hemisphereBy"))
      assertEquals("SunSign", JsonPath.read<String>(raw, "$ew.yearMonthConfig.monthConfig.monthImpl"))
    }
  }
}
