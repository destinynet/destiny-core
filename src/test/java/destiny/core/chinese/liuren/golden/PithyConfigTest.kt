/**
 * Created by smallufo on 2021-09-22.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.AbstractConfigTest
import destiny.core.astrology.DayNightConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.Branch
import destiny.core.chinese.MonthMaster
import destiny.core.chinese.Tianyi
import destiny.core.chinese.liuren.Clockwise
import destiny.core.chinese.liuren.GeneralSeq
import destiny.core.chinese.liuren.GeneralStemBranch
import destiny.core.chinese.liuren.golden.PithyConfigBuilder.Companion.pithyConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class PithyConfigTest : AbstractConfigTest<PithyConfig>() {

  override val serializer: KSerializer<PithyConfig> = PithyConfig.serializer()

  override val configByConstructor: PithyConfig = PithyConfig(
    direction = Branch.丑,
    eightWordsConfig = EightWordsConfig(),
    monthMaster = MonthMaster.Combined,
    clockwise = Clockwise.DayNightFixed,
    dayNightConfig = DayNightConfig(impl = DayNightConfig.DayNightImpl.Half),
    tianyi = Tianyi.ZiweiBook,
    generalSeq = GeneralSeq.Zhao,
    generalStemBranch = GeneralStemBranch.Liuren
  )
  override val configByFunction: PithyConfig = pithyConfig {
    direction = Branch.丑
    monthMaster = MonthMaster.Combined
    clockwise = Clockwise.DayNightFixed
    dayNightConfig = DayNightConfig(impl = DayNightConfig.DayNightImpl.Half)
    tianyi = Tianyi.ZiweiBook
    generalSeq = GeneralSeq.Zhao
    generalStemBranch = GeneralStemBranch.Liuren
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""direction":\s*"丑"""".toRegex()))
    assertTrue(raw.contains(""""monthMaster":\s*"Combined"""".toRegex()))
    assertTrue(raw.contains(""""clockwise":\s*"DayNightFixed"""".toRegex()))
    assertTrue(raw.contains(""""impl":\s*"Half"""".toRegex()))
    assertTrue(raw.contains(""""tianyi":\s*"ZiweiBook"""".toRegex()))
    assertTrue(raw.contains(""""generalSeq":\s*"Zhao"""".toRegex()))
    assertTrue(raw.contains(""""generalStemBranch":\s*"Liuren"""".toRegex()))
  }
}
