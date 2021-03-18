/**
 * Created by smallufo on 2021-03-18.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.*
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail


class ILunarStationDailyTest {

  @Test
  fun testLeader() {
    assertSame(虛, ILunarStationDaily.getLeader(1, 1))
    assertSame(張, ILunarStationDaily.getLeader(1, 2))
    assertSame(室, ILunarStationDaily.getLeader(1, 3))
    assertSame(軫, ILunarStationDaily.getLeader(1, 4))

    assertSame(箕, ILunarStationDaily.getLeader(7, 1))
    assertSame(井, ILunarStationDaily.getLeader(7, 2))
    assertSame(牛, ILunarStationDaily.getLeader(7, 3))
    assertSame(柳, ILunarStationDaily.getLeader(7, 4))

    try {
      ILunarStationDaily.getLeader(0, 1)
      fail()
    } catch (e: IllegalArgumentException) {
      assertTrue(true)
    }

    try {
      ILunarStationDaily.getLeader(8, 1)
      fail()
    } catch (e: IllegalArgumentException) {
      assertTrue(true)
    }

    try {
      ILunarStationDaily.getLeader(1, 0)
      fail()
    } catch (e: IllegalArgumentException) {
      assertTrue(true)
    }

    try {
      ILunarStationDaily.getLeader(1, 5)
      fail()
    } catch (e: IllegalArgumentException) {
      assertTrue(true)
    }
  }
}
