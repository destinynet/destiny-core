/**
 * Created by smallufo on 2021-03-11.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.*
import kotlin.test.Test
import kotlin.test.assertSame

internal class LunarStationMonthlyAoHeadTest {

  /**
   * 例如：2008年是箕水豹年，根據歌訣金心土胃水騎牛，水宿之年的
   * 正月用「牛」，這個牛就是牛金牛，因此2008年
   * 正月為牛金牛，
   * 二月為女土蝠，
   * 三月為虛日鼠，
   * 四月為危月燕，
   * 五月為室火豬。
   */
  @Test
  fun testMonth() {
    val impl = LunarStationMonthlyAoHead()
    assertSame(牛 , impl.getMonthly(箕 , 1))
    assertSame(女 , impl.getMonthly(箕 , 2))
    assertSame(虛 , impl.getMonthly(箕 , 3))
    assertSame(危 , impl.getMonthly(箕 , 4))
    assertSame(室 , impl.getMonthly(箕 , 5))
  }
}
