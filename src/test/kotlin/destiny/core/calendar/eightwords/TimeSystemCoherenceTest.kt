/**
 * Created by Claude on 2026-08-15.
 *
 * [coherentMidnight] / [isTimeSystemCoherent] 的純函數契約。
 *
 * 子正依定義是子時的中點，因此它與時辰切割必須用同一套時刻系統。配錯的兩種組合
 * 在經度遠離時區中線處會讓「子正」落到「子初」之前，日柱因而整天偏一柱 ——
 * 不拋例外，在近中線處也看不出來。行為層的實測在 destiny-core-impl 的
 * `MidnightHourImplCoherenceTest`（那裡要起 Spring 才拿得到 feature）；
 * 這裡只釘對應關係本身。
 */
package destiny.core.calendar.eightwords

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TimeSystemCoherenceTest {

  /** 只有兩種自洽配對；每個 [HourImpl] 都要有對應，不能有漏 */
  @Test
  fun `每個時辰切割都有唯一自洽的子正`() {
    assertEquals(MidnightImpl.NADIR, HourImpl.TST.coherentMidnight)
    assertEquals(MidnightImpl.CLOCK0, HourImpl.LMT.coherentMidnight)
    // 新增 HourImpl 時必須一併決定它的子正 —— when 是窮舉的，漏了就編不過，
    // 但這條讓「新增之後對應錯」也會紅
    assertEquals(HourImpl.entries.size, HourImpl.entries.map { it.coherentMidnight }.toSet().size,
                 "兩個時刻系統不該共用同一個子正定義")
  }

  @Test
  fun `自洽的組合`() {
    assertTrue(DayHourConfig(
      DayConfig(midnight = MidnightImpl.NADIR), HourBranchConfig(hourImpl = HourImpl.TST)).isTimeSystemCoherent)
    assertTrue(DayHourConfig(
      DayConfig(midnight = MidnightImpl.CLOCK0), HourBranchConfig(hourImpl = HourImpl.LMT)).isTimeSystemCoherent)
  }

  /** 真太陽時配鐘面零時、平太陽時配天底 —— 這兩種是靜靜算錯的來源 */
  @Test
  fun `配錯的組合`() {
    assertFalse(DayHourConfig(
      DayConfig(midnight = MidnightImpl.CLOCK0), HourBranchConfig(hourImpl = HourImpl.TST)).isTimeSystemCoherent)
    assertFalse(DayHourConfig(
      DayConfig(midnight = MidnightImpl.NADIR), HourBranchConfig(hourImpl = HourImpl.LMT)).isTimeSystemCoherent)
  }

  /** 站方預設必須是自洽的 —— 否則每一張沒指定設定的盤都走在警告路徑上 */
  @Test
  fun `預設值自洽`() {
    assertTrue(DayHourConfig().isTimeSystemCoherent)
    assertTrue(EightWordsConfig().isTimeSystemCoherent)
  }
}
