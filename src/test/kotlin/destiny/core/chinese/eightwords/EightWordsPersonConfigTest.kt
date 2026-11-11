/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import destiny.core.astrology.*
import destiny.core.calendar.eightwords.*
import kotlinx.serialization.KSerializer
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class EightWordsPersonConfigTest : AbstractConfigTest<EightWordsPersonConfig>() {

  override val serializer: KSerializer<EightWordsPersonConfig> = EightWordsPersonConfig.serializer()

  override val configByConstructor: EightWordsPersonConfig
    get() {
      val ewConfig = EightWordsConfig(
        YearMonthConfig(
          YearConfig(changeYearDegree = 270.0),
          MonthConfig(southernHemisphereOpposition = true, hemisphereBy = HemisphereBy.DECLINATION, monthImpl = MonthImpl.SunSign)
        ),
        DayHourConfig(
          DayConfig(changeDay = ChangeDay.ZI_MIDDLE, midnight = MidnightImpl.CLOCK0),
          HourBranchConfig(
            hourImpl = HourImpl.LMT,
            transConfig = TransConfig(discCenter = true, refraction = false, temperature = 23.0, pressure = 1000.0)
          )
        )
      )

      val ewContextConfig = EightWordsContextConfig(
        ewConfig,
        RisingSignConfig(
          HouseConfig(houseSystem = HouseSystem.EQUAL, coordinate = Coordinate.SIDEREAL),
          TradChineseRisingSignConfig(hourImpl = HourImpl.LMT),
          RisingSignImpl.TradChinese
        ),
        ZodiacSignConfig(star = Planet.SUN)
      )

      return EightWordsPersonConfig(
        ewContextConfig,
        FortuneLargeConfig(impl = FortuneLargeImpl.SolarTermsSpan, span = 90.0, eightWordsConfig = ewConfig),
        FortuneSmallConfig(impl = FortuneSmallConfig.Impl.SixGia, count = 90, intAgeNotes = listOf(IntAgeNote.Minguo), eightWordsConfig = ewConfig),
        EwContextScore.OctaDivide46,
        Locale.JAPANESE
      )
    }

  override val assertion: (String) -> Unit = { raw ->
    // 三份 eightWordsConfig（命盤、大運、小運）都要是同一組設定
    listOf(
      "$.eightwordsContextConfig.eightWordsConfig",
      "$.fortuneLargeConfig.eightWordsConfig",
      "$.fortuneSmallConfig.eightWordsConfig",
    ).forEach { ew ->
      assertEquals(270.0, JsonPath.read<Double>(raw, "$ew.yearMonthConfig.yearConfig.changeYearDegree"))
      assertEquals(true, JsonPath.read<Boolean>(raw, "$ew.yearMonthConfig.monthConfig.southernHemisphereOpposition"))
      assertEquals("DECLINATION", JsonPath.read<String>(raw, "$ew.yearMonthConfig.monthConfig.hemisphereBy"))
      assertEquals("SunSign", JsonPath.read<String>(raw, "$ew.yearMonthConfig.monthConfig.monthImpl"))
      assertEquals("ZI_MIDDLE", JsonPath.read<String>(raw, "$ew.dayHourConfig.dayConfig.changeDay"))
      assertEquals("CLOCK0", JsonPath.read<String>(raw, "$ew.dayHourConfig.dayConfig.midnight"))
      assertEquals("LMT", JsonPath.read<String>(raw, "$ew.dayHourConfig.hourBranchConfig.hourImpl"))
    }

    assertNotNull(JsonPath.read<Any>(raw, "$.eightwordsContextConfig.risingSignConfig.tradChineseRisingSignConfig"))
    assertEquals("LMT", JsonPath.read<String>(raw, "$.eightwordsContextConfig.risingSignConfig.tradChineseRisingSignConfig.hourImpl"))
    assertEquals("SolarTermsSpan", JsonPath.read<String>(raw, "$.fortuneLargeConfig.impl"))
    assertEquals(90.0, JsonPath.read<Double>(raw, "$.fortuneLargeConfig.span"))
    assertEquals("SixGia", JsonPath.read<String>(raw, "$.fortuneSmallConfig.impl"))
    assertEquals(90, JsonPath.read<Int>(raw, "$.fortuneSmallConfig.count"))
    assertEquals(listOf("Minguo"), JsonPath.read<List<String>>(raw, "$.fortuneSmallConfig.intAgeNotes"))
    assertEquals("OctaDivide46", JsonPath.read<String>(raw, "$.ewContextScore"))
    assertEquals("ja", JsonPath.read<String>(raw, "$.locale"))
  }
}
