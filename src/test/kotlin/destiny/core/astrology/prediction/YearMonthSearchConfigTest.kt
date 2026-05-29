/**
 * YearMonthSearchConfig 的純單元測試 —— 集合成員資格驅動 gating 的語意。
 */
package destiny.core.astrology.prediction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class YearMonthSearchConfigTest {

  @Test
  fun defaults_enableEverything() {
    val c = YearMonthSearchConfig()
    assertEquals(PeriodSource.entries.toSet(), c.periodSources)
    assertEquals(3, c.eventSourceConfigs.size)
    assertEquals(12, c.topN)
    assertTrue(c.withLunarReturns)
    PeriodSource.entries.forEach { assertTrue(c.evaluates(it)) }
  }

  @Test
  fun withLunarReturns_derivedFromMembership() {
    val without = YearMonthSearchConfig(periodSources = PeriodSource.entries.toSet() - PeriodSource.LUNAR_RETURN)
    assertFalse(without.withLunarReturns)
    assertFalse(without.evaluates(PeriodSource.LUNAR_RETURN))
    // 其餘段層仍啟用
    assertTrue(without.evaluates(PeriodSource.PROFECTION))
  }

  @Test
  fun evaluates_reflectsConfiguredSubset() {
    val onlyProfection = YearMonthSearchConfig(periodSources = setOf(PeriodSource.PROFECTION))
    assertTrue(onlyProfection.evaluates(PeriodSource.PROFECTION))
    assertFalse(onlyProfection.evaluates(PeriodSource.ZODIACAL_RELEASING))
    assertFalse(onlyProfection.withLunarReturns)
  }
}
