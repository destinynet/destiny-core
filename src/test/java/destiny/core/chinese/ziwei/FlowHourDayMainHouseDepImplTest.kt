/**
 * Created by smallufo on 2017-04-29.
 */
package destiny.core.chinese.ziwei

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class FlowHourDayMainHouseDepImplTest {

  private val logger = KotlinLogging.logger { }

  internal var impl: IFlowHour = FlowHourDayMainHouseDepImpl()

  @Test
  fun testString() {
    assertNotNull(impl.toString(Locale.TAIWAN))
    assertNotNull(impl.toString(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.toString(Locale.TAIWAN), impl.toString(Locale.SIMPLIFIED_CHINESE))
  }
}
