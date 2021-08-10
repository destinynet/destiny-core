/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.DayConfigBuilder.Companion.dayConfig
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DayConfigTest {

  @Test
  fun testInvokeConfigFun() {
    val cdazFalse: DayConfig = dayConfig {
      changeDayAfterZi = false
    }

    assertFalse(cdazFalse.changeDayAfterZi)

    val cdazTrue: DayConfig = dayConfig {
      changeDayAfterZi = true
    }
    assertTrue(cdazTrue.changeDayAfterZi)
  }
}
