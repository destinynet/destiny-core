/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class YearConfigTest {

  @Test
  fun testInvokeConfigFun() {
    val config: YearConfig = yearConfig {
      changeYearDegree = 270.0
    }

    assertEquals(270.0, config.changeYearDegree)
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
