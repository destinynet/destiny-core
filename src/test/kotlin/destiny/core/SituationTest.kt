/**
 * Created by smallufo on 2026-09-02.
 */
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

  /** 總量釘住 —— 與下面的逐格互補：這一條抓「幾格被改了」，那一條抓「哪一格」。 */
  @Test
  fun `roles 分佈`() {
    val bySize = Situation.entries.groupingBy { it.roles.size }.eachCount()
    assertEquals(mapOf(2 to 38, 1 to 19), bySize)
  }

  /**
   * 逐格釘住 19 個單值成員 —— 偏離時失敗訊息會直接指名是哪一格被改了填格。
   *
   * 為什麼不靠上面那三個聚合數字：它們是**總量**。把 `GRADUATION` 改成承受端、
   * 同時把 `SPIRITUAL_AWAKENING` 反向改，數字一字不變、測試全綠 ——
   * 而那正是「有人改了填格原則」，也就是這裡宣稱要抓的事。
   *
   * 只釘單值：兩值是預設（另一端有可能，交給事件層），單值才是有人**做過排除判斷**的地方。
   */
  @Test
  fun `單值成員逐格釘住`() {
    val single = Situation.entries.filter { it.roles.size == 1 }
      .associate { it.name to it.roles.single() }
    assertEquals(
      mapOf(
        "FAMILY_LOSS" to EventRole.RECIPIENT,
        "FAMILY_HEALTH_CRISIS" to EventRole.RECIPIENT,
        "GRADUATION" to EventRole.INITIATOR,
        "ACADEMIC_SETBACK" to EventRole.RECIPIENT,
        "ENTREPRENEURSHIP" to EventRole.INITIATOR,
        "UNEMPLOYMENT_LAYOFF" to EventRole.RECIPIENT,
        "INHERITANCE_WINDFALL" to EventRole.RECIPIENT,
        "REAL_ESTATE_PURCHASE" to EventRole.INITIATOR,
        "MAJOR_ILLNESS" to EventRole.RECIPIENT,
        "HEALTH_RECOVERY" to EventRole.RECIPIENT,
        "PRIVACY_LOSS" to EventRole.RECIPIENT,
        "LEGAL_OUTCOME_FAVORABLE" to EventRole.RECIPIENT,
        "LEGAL_OUTCOME_UNFAVORABLE" to EventRole.RECIPIENT,
        "FREEDOM_LOSS" to EventRole.RECIPIENT,
        "COMPETITION_WIN" to EventRole.INITIATOR,
        "COMPETITION_LOSS" to EventRole.INITIATOR,
        "PUBLIC_CONFRONTATION" to EventRole.INITIATOR,
        "SPIRITUAL_AWAKENING" to EventRole.RECIPIENT,
        "PILGRIMAGE" to EventRole.INITIATOR,
      ),
      single
    )
  }

  /**
   * 每一個 [EventCategory] 至少要有一個成員。
   *
   * category 錯填是**靜默**的 —— 只會讓選項出現在錯誤的群組裡，沒有任何斷言會紅。
   * 這一條擋的是最粗的那一種：整個群組空掉（成員被搬走或分類被誤改）。
   */
  @Test
  fun `每個 EventCategory 至少有一個成員`() {
    val covered = Situation.entries.map { it.category }.toSet()
    assertEquals(EventCategory.entries.toSet(), covered, "有 EventCategory 沒有任何 Situation 成員")
  }

  /** 舊字彙的殘留：靠名稱區分主詞的雙生子與形容詞開頭的成員都必須消失。 */
  @Test
  fun `舊字彙不得殘留`() {
    val names = Situation.entries.map { it.name }.toSet()
    listOf("VICTIM_OF_ATTACK", "VIOLENT_OFFENDER", "FAVORABLE_LEGAL_OUTCOME", "LEGAL_CHALLENGE")
      .forEach { assertTrue(it !in names, "$it 應已被取代") }
  }
}
