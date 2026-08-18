/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FlowMonthYearMainHouseDepImplTest {

  internal var impl: IFlowMonth = FlowMonthYearMainHouseDepImpl()

  /**
   * 比對 : http://imgur.com/Xz3tQkP
   *
   * 流年在申、欲求的流月為亥 → 流月命宮落在巳。原本只有 logger.info，算錯不會紅。
   */
  @Test
  fun getFlowMonth() {
    assertSame(巳, impl.getFlowMonth(申, 亥, 1, 子))

    // 「以流年命宮起正月」：正月（寅）的流月命宮即流年宮位本身，其後逐月順行
    assertSame(申, impl.getFlowMonth(申, 寅, 1, 子))
    assertSame(酉, impl.getFlowMonth(申, 卯, 1, 子))
    assertSame(戌, impl.getFlowMonth(申, 辰, 1, 子))

    // 12 個流月剛好走完 12 宮，不重複
    assertEquals(
      Branch.entries.toSet(),
      Branch.entries.map { impl.getFlowMonth(申, it, 1, 子) }.toSet()
    )
  }

  /**
   * 這個實作（紫雲派：以流年命宮起流月正月）**刻意不看生月與生時** —— 結果只由流年與流月決定。
   *
   * 原測試只 log 單一組數值，「後兩個參數其實用不到」這件事完全看不出來；
   * 換成別的 [IFlowMonth] 實作時，這正是最需要留意的差異。
   */
  @Test
  fun `生月與生時不影響此實作`() {
    Branch.entries.forEach { birthHour ->
      (1..12).forEach { birthMonth ->
        assertSame(巳, impl.getFlowMonth(申, 亥, birthMonth, birthHour))
      }
    }
  }

}
