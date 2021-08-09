/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertFalse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DayConfigTest {

  @Test
  fun testInvokeConfigFun() {
    val config: DayConfig = dayConfig {
      changeDayAfterZi = false
    }

    assertFalse(config.changeDayAfterZi)
  }
}
