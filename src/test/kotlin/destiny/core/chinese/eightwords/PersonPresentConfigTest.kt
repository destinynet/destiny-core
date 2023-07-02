/**
 * Created by smallufo on 2021-08-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.eightwords.PersonPresentConfigBuilder.Companion.ewPersonPresent
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

  override val configByFunction: PersonPresentConfig
    get() {
      return with(EightWordsPersonConfig()) {
        ewPersonPresent {
          changeYearDegree = 270.0
          southernHemisphereOpposition = true
          hemisphereBy = HemisphereBy.DECLINATION
          monthImpl = MonthImpl.SunSign
          viewGmt = GmtJulDay(0.0)
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertFalse(raw.contains(""""changeYearDegree":\s*315.0""".toRegex()))

    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertFalse(raw.contains(""""southernHemisphereOpposition":\s*false""".toRegex()))

    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertFalse(raw.contains(""""hemisphereBy":\s*"EQUATOR"""".toRegex()))

    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))
    assertFalse(raw.contains(""""monthImpl":\s*"SolarTerms"""".toRegex()))
  }
}
