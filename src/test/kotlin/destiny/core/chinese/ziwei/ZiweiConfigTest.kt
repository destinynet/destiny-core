/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.eightwords.ChangeDay
import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.DayConfig
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.MidnightImpl
import destiny.core.chinese.AgeType
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YearType
import destiny.core.chinese.ziwei.ZiweiConfigBuilder.Companion.ziweiConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class ZiweiConfigTest : AbstractConfigTest<ZiweiConfig>() {

  override val serializer: KSerializer<ZiweiConfig> = ZiweiConfig.serializer()

  override val configByConstructor: ZiweiConfig = ZiweiConfig(
    setOf(
      *StarMain.values, *StarMinor.values, *StarLucky.values, *StarUnlucky.values,
      *StarDoctor.values, *StarGeneralFront.values, *StarLongevity.values, *StarYearFront.values
    ),
    MainBodyHouse.Astro,
    PurpleStarBranch.LeapAccumDays,
    MonthAlgo.MONTH_SOLAR_TERMS,
    MonthAlgo.MONTH_LEAP_SPLIT15,
    YearType.YEAR_SOLAR,
    HouseSeq.Astro,
    Tianyi.Ocean,
    FireBell.FIREBELL_COLLECT,
    SkyHorse.MONTH,
    HurtAngel.HURT_ANGEL_YINYANG,
    RedBeauty.RED_BEAUTY_SAME,
    TransFour.Ziyun,
    Strength.Middle,
    FlowYear.Anchor,
    FlowMonth.Fixed,
    FlowDay.SkipFlowMonthMainHouse,
    FlowHour.Branch,
    BigRange.SkipMain,
    AgeType.REAL,
    EightWordsConfig(
      dayHourConfig = DayHourConfig(
        DayConfig(changeDay = ChangeDay.ZI_MIDDLE, midnight = MidnightImpl.CLOCK0),
      )
    )
  )

  override val configByFunction: ZiweiConfig
    get() {

      val ewConfig = EightWordsConfig(
        dayHourConfig = DayHourConfig(
          DayConfig(changeDay = ChangeDay.ZI_MIDDLE, midnight = MidnightImpl.CLOCK0),
        )
      )

      return with(ewConfig) {
        ziweiConfig {
          stars = setOf(
            *StarMain.values, *StarMinor.values, *StarLucky.values, *StarUnlucky.values,
            *StarDoctor.values, *StarGeneralFront.values, *StarLongevity.values, *StarYearFront.values
          )
          mainBodyHouse = MainBodyHouse.Astro
          purpleStarBranch = PurpleStarBranch.LeapAccumDays
          mainStarsAlgo = MonthAlgo.MONTH_SOLAR_TERMS
          monthStarsAlgo = MonthAlgo.MONTH_LEAP_SPLIT15
          yearType = YearType.YEAR_SOLAR
          houseSeq = HouseSeq.Astro
          tianyi = Tianyi.Ocean
          fireBell = FireBell.FIREBELL_COLLECT
          skyHorse = SkyHorse.MONTH
          hurtAngel = HurtAngel.HURT_ANGEL_YINYANG
          redBeauty = RedBeauty.RED_BEAUTY_SAME
          transFour = TransFour.Ziyun
          strength = Strength.Middle
          flowYear = FlowYear.Anchor
          flowMonth = FlowMonth.Fixed
          flowDay = FlowDay.SkipFlowMonthMainHouse
          flowHour = FlowHour.Branch
          bigRange = BigRange.SkipMain
          sectionAgeType = AgeType.REAL
        }
      }


    }

  override val assertion: (String) -> Unit = { raw: String ->
    val docCtx = JsonPath.parse(raw)
    assertEquals("Astro", docCtx.read("$.mainBodyHouse", String::class.java))
    assertEquals("LeapAccumDays", docCtx.read("$.purpleStarBranch", String::class.java))
    assertEquals("MONTH_SOLAR_TERMS", docCtx.read("$.mainStarsAlgo", String::class.java))
    assertEquals("MONTH_LEAP_SPLIT15", docCtx.read("$.monthStarsAlgo", String::class.java))
    assertEquals("YEAR_SOLAR", docCtx.read("$.yearType", String::class.java))
    assertEquals("Astro", docCtx.read("$.houseSeq", String::class.java))
    assertEquals("Ocean", docCtx.read("$.tianyi", String::class.java))
    assertEquals("FIREBELL_COLLECT", docCtx.read("$.fireBell", String::class.java))
    assertEquals("MONTH", docCtx.read("$.skyHorse", String::class.java))
    assertEquals("HURT_ANGEL_YINYANG", docCtx.read("$.hurtAngel", String::class.java))
    assertEquals("RED_BEAUTY_SAME", docCtx.read("$.redBeauty", String::class.java))
    assertEquals("Ziyun", docCtx.read("$.transFour", String::class.java))
    assertEquals("Middle", docCtx.read("$.strength", String::class.java))
    assertEquals("Anchor", docCtx.read("$.flowYear", String::class.java))
    assertEquals("Fixed", docCtx.read("$.flowMonth", String::class.java))
    assertEquals("SkipFlowMonthMainHouse", docCtx.read("$.flowDay", String::class.java))
    assertEquals("Branch", docCtx.read("$.flowHour", String::class.java))
    assertEquals("SkipMain", docCtx.read("$.bigRange", String::class.java))
    assertEquals("REAL", docCtx.read("$.sectionAgeType", String::class.java))
    assertEquals("ZI_MIDDLE", docCtx.read("$.ewConfig.dayHourConfig.dayConfig.changeDay", String::class.java))
    assertEquals("CLOCK0", docCtx.read("$.ewConfig.dayHourConfig.dayConfig.midnight", String::class.java))
  }
}
