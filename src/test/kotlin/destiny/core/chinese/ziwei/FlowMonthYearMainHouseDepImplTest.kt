/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import mu.KotlinLogging
import kotlin.test.Test

class FlowMonthYearMainHouseDepImplTest {

  private val logger = KotlinLogging.logger { }

  internal var impl: IFlowMonth = FlowMonthYearMainHouseDepImpl()

  /**
   * 比對 : http://imgur.com/Xz3tQkP
   */
  @Test
  fun getFlowMonth() {
    val b = impl.getFlowMonth(申, 亥, 1, 子)
    logger.info("b = {}", b)
  }

}
