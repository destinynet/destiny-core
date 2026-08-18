/**
 * Created by smallufo on 2026-08-18.
 */
package destiny.core

import destiny.core.astrology.BirthDataGrain
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * [SynastryGrain] 由四態 enum 改為攜帶兩造 [BirthDataGrain] 的 data class。
 *
 * 舊的 2×2 是**有損的**：時辰生被壓成 DATE，合盤警告因此會說「對方出生時間未知、
 * 月亮不可用」—— 但時辰已知，月亮誤差僅 ±0.55°。
 */
class SynastryGrainTest {

  /** 四個舊組合的字串形式必須完全不變 —— DB 的 JSONB 與 chatbot 的 context map 都靠它。 */
  @Test
  fun legacyStringFormIsUnchanged() {
    val legacy = mapOf(
      SynastryGrain.BOTH_FULL to "BOTH_FULL",
      SynastryGrain.INNER_FULL_OUTER_DATE to "INNER_FULL_OUTER_DATE",
      SynastryGrain.INNER_DATE_OUTER_FULL to "INNER_DATE_OUTER_FULL",
      SynastryGrain.BOTH_DATE to "BOTH_DATE",
    )
    legacy.forEach { (grain, name) ->
      assertEquals(name, grain.name)
      assertEquals("\"$name\"", Json.encodeToString<SynastryGrain>(grain))
      assertEquals(grain, Json.decodeFromString<SynastryGrain>("\"$name\""))
      assertEquals(grain, SynastryGrain.of(name))
    }
  }

  /** 舊四名各自對應的兩造精度。 */
  @Test
  fun legacyAliasesMapToBothSides() {
    assertEquals(BirthDataGrain.MINUTE, SynastryGrain.BOTH_FULL.inner)
    assertEquals(BirthDataGrain.MINUTE, SynastryGrain.BOTH_FULL.outer)
    assertEquals(BirthDataGrain.MINUTE, SynastryGrain.INNER_FULL_OUTER_DATE.inner)
    assertEquals(BirthDataGrain.DAY, SynastryGrain.INNER_FULL_OUTER_DATE.outer)
    assertEquals(BirthDataGrain.DAY, SynastryGrain.INNER_DATE_OUTER_FULL.inner)
    assertEquals(BirthDataGrain.MINUTE, SynastryGrain.INNER_DATE_OUTER_FULL.outer)
    // innerGrain / outerGrain 這兩個既有 extension 仍可用
    assertEquals(BirthDataGrain.DAY, SynastryGrain.BOTH_DATE.innerGrain)
    assertEquals(BirthDataGrain.DAY, SynastryGrain.BOTH_DATE.outerGrain)
  }

  /** 舊 enum 表達不出來的組合 —— 這才是這次改動的重點。 */
  @Test
  fun newCombinationsRoundTrip() {
    val hour2Inner = SynastryGrain(BirthDataGrain.HOUR2, BirthDataGrain.MINUTE)
    assertEquals("HOUR2|MINUTE", hour2Inner.name)
    assertEquals("\"HOUR2|MINUTE\"", Json.encodeToString<SynastryGrain>(hour2Inner))
    assertEquals(hour2Inner, Json.decodeFromString<SynastryGrain>("\"HOUR2|MINUTE\""))

    val nocturnal = SynastryGrain(BirthDataGrain.DAY_NIGHT_NOCTURNAL, BirthDataGrain.HOUR2)
    assertEquals("DAY_NIGHT_NOCTURNAL|HOUR2", nocturnal.name)
    assertEquals(nocturnal, SynastryGrain.of(nocturnal.name))

    // 全部 5×5 組合都寫得出也讀得回
    BirthDataGrain.entries.forEach { i ->
      BirthDataGrain.entries.forEach { o ->
        val g = SynastryGrain(i, o)
        assertEquals(g, SynastryGrain.of(g.name), "round-trip failed for $g")
      }
    }
  }

  @Test
  fun unknownStringFails() {
    assertFailsWith<IllegalArgumentException> { SynastryGrain.of("NOPE") }
    assertNull(SynastryGrain.ofOrNull("NOPE"))
    assertNull(SynastryGrain.ofOrNull(null))
  }

  /**
   * 舊四名的**讀取**支援不可移除 —— 那些字串加密在使用者的 LINE 聊天記錄裡，
   * 我們查不到也改不了（見 [SynastryGrain.Companion.of] 的 KDoc）。
   *
   * 這支測試是給未來想清理「死碼」的人看的：把 of() 的相容層拿掉，它會轉紅。
   */
  @Test
  fun legacyNamesMustStayReadable() {
    listOf("BOTH_FULL", "INNER_FULL_OUTER_DATE", "INNER_DATE_OUTER_FULL", "BOTH_DATE").forEach { legacy ->
      assertEquals(
        SynastryGrain.of(legacy), Json.decodeFromString<SynastryGrain>("\"$legacy\""),
        "$legacy 必須永遠讀得回來 —— 使用者 LINE 聊天記錄裡的舊訊息帶的就是這個字串"
      )
    }
  }
}
