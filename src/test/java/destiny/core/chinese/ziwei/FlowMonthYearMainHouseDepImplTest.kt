/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class FlowMonthYearMainHouseDepImplTest {

  private val logger = KotlinLogging.logger { }

  internal var impl: IFlowMonth = FlowMonthYearMainHouseDepImpl()

  @Test
  fun testString() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
  }

  /**
   * 比對 : http://imgur.com/Xz3tQkP
   */
  @Test
  fun getFlowMonth() {
    val b = impl.getFlowMonth(申, 亥, 1, 子)
    logger.info("b = {}", b)
  }

}
