/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.chinese.AgeType
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YearType
import destiny.core.chinese.ziwei.ZiweiConfigBuilder.Companion.ziweiConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

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
    listOf(IntAgeNote.WestYear)
  )

  override val configByFunction: ZiweiConfig = ziweiConfig {
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
    ageNotes = listOf(IntAgeNote.WestYear)
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""mainBodyHouse":\s*"Astro"""".toRegex()))
    assertTrue(raw.contains(""""purpleStarBranch":\s*"LeapAccumDays"""".toRegex()))
    assertTrue(raw.contains(""""mainStarsAlgo":\s*"MONTH_SOLAR_TERMS"""".toRegex()))
    assertTrue(raw.contains(""""monthStarsAlgo":\s*"MONTH_LEAP_SPLIT15"""".toRegex()))
    assertTrue(raw.contains(""""yearType":\s*"YEAR_SOLAR"""".toRegex()))
    assertTrue(raw.contains(""""houseSeq":\s*"Astro"""".toRegex()))
    assertTrue(raw.contains(""""tianyi":\s*"Ocean"""".toRegex()))
    assertTrue(raw.contains(""""fireBell":\s*"FIREBELL_COLLECT"""".toRegex()))
    assertTrue(raw.contains(""""skyHorse":\s*"MONTH"""".toRegex()))
    assertTrue(raw.contains(""""hurtAngel":\s*"HURT_ANGEL_YINYANG"""".toRegex()))
    assertTrue(raw.contains(""""redBeauty":\s*"RED_BEAUTY_SAME"""".toRegex()))
    assertTrue(raw.contains(""""transFour":\s*"Ziyun"""".toRegex()))
    assertTrue(raw.contains(""""strength":\s*"Middle"""".toRegex()))
    assertTrue(raw.contains(""""flowYear":\s*"Anchor"""".toRegex()))
    assertTrue(raw.contains(""""flowMonth":\s*"Fixed"""".toRegex()))
    assertTrue(raw.contains(""""flowDay":\s*"SkipFlowMonthMainHouse"""".toRegex()))
    assertTrue(raw.contains(""""flowHour":\s*"Branch"""".toRegex()))
    assertTrue(raw.contains(""""bigRange":\s*"SkipMain"""".toRegex()))
    assertTrue(raw.contains(""""sectionAgeType":\s*"REAL"""".toRegex()))
    assertTrue(raw.contains(""""ageNotes":\s*\[\s*"WestYear"\s*]""".toRegex()))
  }
}
