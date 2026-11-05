/**
 * Created by smallufo on 2021-09-22.
 */
package destiny.core.chinese.liuren.golden

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.DayNightConfig
import destiny.core.astrology.DayNightImpl
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.Branch
import destiny.core.chinese.MonthMaster
import destiny.core.chinese.Tianyi
import destiny.core.chinese.liuren.Clockwise
import destiny.core.chinese.liuren.GeneralSeq
import destiny.core.chinese.liuren.GeneralStemBranch
import destiny.core.chinese.liuren.golden.PithyConfigBuilder.Companion.pithyConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class PithyConfigTest : AbstractConfigTest<PithyConfig>() {

  override val serializer: KSerializer<PithyConfig> = PithyConfig.serializer()

  override val configByConstructor: PithyConfig = PithyConfig(
    direction = Branch.丑,
    eightWordsConfig = EightWordsConfig(),
    monthMaster = MonthMaster.Combined,
    clockwise = Clockwise.DayNightFixed,
    dayNightConfig = DayNightConfig(dayNightImpl = DayNightImpl.Half),
    tianyi = Tianyi.ZiweiBook,
    generalSeq = GeneralSeq.Zhao,
    generalStemBranch = GeneralStemBranch.Liuren
  )
  override val configByFunction: PithyConfig
    get() {
      return with(EightWordsConfig()) {
        pithyConfig {
          direction = Branch.丑
          monthMaster = MonthMaster.Combined
          clockwise = Clockwise.DayNightFixed
          dayNightConfig = DayNightConfig(dayNightImpl = DayNightImpl.Half)
          tianyi = Tianyi.ZiweiBook
          generalSeq = GeneralSeq.Zhao
          generalStemBranch = GeneralStemBranch.Liuren
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("丑", JsonPath.read<String>(raw, "$.direction"))
    assertEquals("Combined", JsonPath.read<String>(raw, "$.monthMaster"))
    assertEquals("DayNightFixed", JsonPath.read<String>(raw, "$.clockwise"))
    assertEquals("Half", JsonPath.read<String>(raw, "$.dayNightConfig.dayNightImpl"))
    assertEquals("ZiweiBook", JsonPath.read<String>(raw, "$.tianyi"))
    assertEquals("Zhao", JsonPath.read<String>(raw, "$.generalSeq"))
    assertEquals("Liuren", JsonPath.read<String>(raw, "$.generalStemBranch"))
  }
}
