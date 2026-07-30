/**
 * Created by smallufo on 2026-07-31.
 */
package destiny.core.astrology

import destiny.core.DayNight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BirthDataGrainTest {

  /** DAY_NIGHT 兩個 entry 各自攜帶固定的 [DayNight] payload；既有兩級無 payload */
  @Test
  fun dayNightPayload() {
    assertEquals(DayNight.DAY, BirthDataGrain.DAY_NIGHT_DIURNAL.dayNight)
    assertEquals(DayNight.NIGHT, BirthDataGrain.DAY_NIGHT_NOCTURNAL.dayNight)
    assertNull(BirthDataGrain.DAY.dayNight)
    assertNull(BirthDataGrain.MINUTE.dayNight)
  }

  /**
   * 知晝夜（不知時刻）解鎖 firdaria —— 主運序列僅依晝夜生決定，
   * 且週期以年計，正午錨定的 ±12h 誤差可忽略；axis / profection 仍需 ASC/宮位，維持關閉。
   */
  @Test
  fun capabilities() {
    assertTrue(BirthDataGrain.DAY_NIGHT_DIURNAL.includeFirdaria)
    assertTrue(BirthDataGrain.DAY_NIGHT_NOCTURNAL.includeFirdaria)
    assertFalse(BirthDataGrain.DAY_NIGHT_DIURNAL.includeAxis)
    assertFalse(BirthDataGrain.DAY_NIGHT_NOCTURNAL.includeAxis)
    assertFalse(BirthDataGrain.DAY_NIGHT_DIURNAL.includeProfection)
    assertFalse(BirthDataGrain.DAY_NIGHT_NOCTURNAL.includeProfection)
    // 既有兩級行為不變
    assertFalse(BirthDataGrain.DAY.includeFirdaria)
    assertTrue(BirthDataGrain.MINUTE.includeFirdaria)
    assertTrue(BirthDataGrain.MINUTE.includeAxis)
    assertFalse(BirthDataGrain.DAY.includeAxis)
  }
}
