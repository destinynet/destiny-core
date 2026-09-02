package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SituationTest {

  /** 成員總數釘住。改動數量必須是刻意的，不是順手加的。 */
  @Test
  fun `成員總數為 57`() = assertEquals(57, Situation.entries.size)

  /**
   * `roles` 不得為空。
   *
   * 空集的語意是「主詞這根軸不適用」，與事件層的「這一筆分不出來」無關。
   * 逐格掃描後**沒有任何成員需要它** —— 凡是進得了個人時間軸的事，
   * 本人不是動作端就是承受端。Set 型別免費允許它，本測試把它擋在門外，
   * 等真的出現案例再連同 UI 的第三條分支一起放寬。
   */
  @Test
  fun `每個 situation 至少有一個 role`() =
    assertTrue(Situation.entries.all { it.roles.isNotEmpty() }, "roles 不得為空")

  /** 只有 DEATH 不可預測。 */
  @Test
  fun `只有 DEATH 不可預測`() =
    assertEquals(setOf(Situation.DEATH), Situation.entries.filterNot { it.forecastable }.toSet())

  /** 分佈釘住 —— 「排除不可能」原則的產物。偏離即代表有人改了填格原則。 */
  @Test
  fun `roles 分佈`() {
    val bySize = Situation.entries.groupingBy { it.roles.size }.eachCount()
    assertEquals(mapOf(2 to 36, 1 to 21), bySize)
    assertEquals(14, Situation.entries.count { it.roles == setOf(EventRole.RECIPIENT) })
    assertEquals(7, Situation.entries.count { it.roles == setOf(EventRole.INITIATOR) })
  }

  /** 舊字彙的殘留：靠名稱區分主詞的雙生子與形容詞開頭的成員都必須消失。 */
  @Test
  fun `舊字彙不得殘留`() {
    val names = Situation.entries.map { it.name }.toSet()
    listOf("VICTIM_OF_ATTACK", "VIOLENT_OFFENDER", "FAVORABLE_LEGAL_OUTCOME", "LEGAL_CHALLENGE")
      .forEach { assertTrue(it !in names, "$it 應已被取代") }
  }
}
