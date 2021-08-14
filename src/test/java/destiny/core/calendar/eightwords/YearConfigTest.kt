/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.YearConfigBuilder.Companion.yearConfig
import kotlinx.serialization.KSerializer
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertTrue
import kotlin.test.fail

internal class YearConfigTest : AbstractConfigTest<YearConfig>() {

  override val serializer: KSerializer<YearConfig> = YearConfig.serializer()

  override val configByConstructor: YearConfig = YearConfig(270.0)

  override val configByFunction: YearConfig = yearConfig {
    changeYearDegree = 270.0
  }


  override val assertion = { raw: String ->
    logger.info { raw }
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
  }

  /**
   * 測試不合法的換年時段
   * 合法換年時段的黃道度數為 180(秋分)~~~360,0(春分)
   * 因此，0 < degree < 180 者將被丟出 RuntimeException
   */
  private fun invalidChangeYearDegrees() = Stream.of(1.0, 179.0, 360.0, 361.0)

  @ParameterizedTest
  @MethodSource("invalidChangeYearDegrees")
  fun testInvalidChangeYearDegree(degree: Double) {

    try {
      yearConfig {
        changeYearDegree = degree
      }
      fail()
    } catch (e: RuntimeException) {
      assertTrue(true)
    }
  }
}
