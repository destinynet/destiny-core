/**
 * Created by smallufo on 2026-07-31.
 */
package destiny.core.astrology

import destiny.core.DayNight
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BirthDataGrainTest {

  /** 舊 enum entry 名保留在 companion 上，且與直接建構的實例相等（data class equality）。 */
  @Test
  fun legacyAliases() {
    assertEquals(DayNight.DAY, BirthDataGrain.DAY_NIGHT_DIURNAL.value)
    assertEquals(DayNight.NIGHT, BirthDataGrain.DAY_NIGHT_NOCTURNAL.value)
    assertEquals(BirthDataGrain.DayNightOnly(DayNight.DAY), BirthDataGrain.DAY_NIGHT_DIURNAL)
    assertEquals(BirthDataGrain.DayNightOnly(DayNight.NIGHT), BirthDataGrain.DAY_NIGHT_NOCTURNAL)
  }

  /**
   * 序列化／持久化的字串形式必須與 enum 時代 byte-identical ——
   * DB 舊資料與既存的 LLM 素材都靠它，不可改動。
   */
  @Test
  fun stringFormIsStable() {
    val expected = mapOf(
      BirthDataGrain.DAY to "DAY",
      BirthDataGrain.DAY_NIGHT_DIURNAL to "DAY_NIGHT_DIURNAL",
      BirthDataGrain.DAY_NIGHT_NOCTURNAL to "DAY_NIGHT_NOCTURNAL",
      BirthDataGrain.HOUR2 to "HOUR2",
      BirthDataGrain.MINUTE to "MINUTE",
    )
    expected.forEach { (grain, name) ->
      assertEquals(name, grain.name)
      // sealed class 的預設多型序列化會產出物件，這裡必須是裸字串
      assertEquals("\"$name\"", Json.encodeToString<BirthDataGrain>(grain))
      assertEquals(grain, Json.decodeFromString<BirthDataGrain>("\"$name\""))
      assertEquals(grain, BirthDataGrain.of(name))
    }
    assertEquals(expected.keys.toList(), BirthDataGrain.entries)
  }

  @Test
  fun ofOrNull() {
    assertSame(BirthDataGrain.MINUTE, BirthDataGrain.ofOrNull("MINUTE"))
    assertNull(BirthDataGrain.ofOrNull("NOPE"))
    assertNull(BirthDataGrain.ofOrNull(null))
  }

  /**
   * 能力閘門。時辰級（HOUR2）解鎖本命月亮（±0.55°，落在 orb 內），
   * 但 ASC ±15° 故軸點／小限／月返照仍全關。
   */
  @Test
  fun capabilities() {
    // 軸點：只有 MINUTE
    assertTrue(BirthDataGrain.MINUTE.includeAxis)
    BirthDataGrain.entries.filter { it != BirthDataGrain.MINUTE }.forEach {
      assertFalse(it.includeAxis, "$it should not include axis")
      assertFalse(it.includeProfection, "$it should not include profection")
      assertFalse(it.includeLunarReturns, "$it should not include lunar returns")
    }

    // 本命月亮：MINUTE 與 HOUR2
    assertTrue(BirthDataGrain.MINUTE.includeLunarPosition)
    assertTrue(BirthDataGrain.HOUR2.includeLunarPosition)
    assertFalse(BirthDataGrain.DAY.includeLunarPosition)
    assertFalse(BirthDataGrain.DAY_NIGHT_DIURNAL.includeLunarPosition)
    assertFalse(BirthDataGrain.DAY_NIGHT_NOCTURNAL.includeLunarPosition)
  }

  /** 除 HOUR2 外，晝夜來源純由型別決定，不得觸發 resolver。 */
  @Test
  fun dayNightSourceWithoutResolver() {
    var resolverCalls = 0
    val resolver = { resolverCalls++; DayNightSource.Indeterminate }

    assertEquals(DayNightSource.Unavailable, BirthDataGrain.DAY.resolveDayNightSource(resolver))
    assertEquals(DayNightSource.Known(DayNight.DAY), BirthDataGrain.DAY_NIGHT_DIURNAL.resolveDayNightSource(resolver))
    assertEquals(DayNightSource.Known(DayNight.NIGHT), BirthDataGrain.DAY_NIGHT_NOCTURNAL.resolveDayNightSource(resolver))
    assertEquals(DayNightSource.FromChart, BirthDataGrain.MINUTE.resolveDayNightSource(resolver))
    assertEquals(0, resolverCalls, "resolver 只該為 HOUR2 而跑 —— 它背後是 swisseph")

    assertEquals(DayNightSource.Indeterminate, BirthDataGrain.HOUR2.resolveDayNightSource(resolver))
    assertEquals(1, resolverCalls)
  }

  /**
   * 八字時柱／紫微命宮只需**時辰**，不需分鐘 —— 這是唯一一個 HOUR2 與 MINUTE 等價的閘門。
   * 與 [includeAxis] 對照：ASC 需要分鐘，所以那個閘門對 HOUR2 是 false。
   */
  @Test
  fun chineseHourNeedsOnlyTheDoubleHour() {
    assertTrue(BirthDataGrain.MINUTE.includeChineseHour)
    assertTrue(BirthDataGrain.HOUR2.includeChineseHour)
    assertFalse(BirthDataGrain.DAY.includeChineseHour)
    assertFalse(BirthDataGrain.DAY_NIGHT_DIURNAL.includeChineseHour)
    assertFalse(BirthDataGrain.DAY_NIGHT_NOCTURNAL.includeChineseHour)

    // 兩個閘門刻意不一致：HOUR2 有時辰、沒有 ASC
    assertTrue(BirthDataGrain.HOUR2.includeChineseHour && !BirthDataGrain.HOUR2.includeAxis)
  }
}
