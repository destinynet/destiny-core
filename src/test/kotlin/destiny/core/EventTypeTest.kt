/**
 * Created by smallufo on 2025-08-10.
 */
package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class EventTypeTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(EventType::class, true)
  }

  /**
   * 四個語系的 key 集合必須與 base bundle 一致 —— [testEnums] 因為 ResourceBundle 的 fallback
   * 而抓不到漏翻譯，理由見 [assertBundleParity]。
   */
  @Test
  fun `每個語系都有翻譯`() {
    assertBundleParity("EventType", EventType.entries.map { it.name }, "destiny/core")
  }

  /**
   * [Agency.INDETERMINATE] 是**事件層**的答案（這一筆分不出來）；
   * 型別層的「不固定」是 `null`。混用會讓 [EventType.fixedAgency] 的兩個用途都失效：
   * 抽取端分不出「該問」與「問了也沒用」，統計端分不出「雙向」與「沒判讀」。
   */
  @Test
  fun `型別層不得使用 INDETERMINATE`() {
    EventType.entries.filter { it.fixedAgency == Agency.INDETERMINATE }
      .takeIf { it.isNotEmpty() }
      ?.let { fail("型別層的『不固定』請用 null，不是 INDETERMINATE：$it") }
  }

  /**
   * 雙向型別是主詞這一維**唯一帶資訊**的地方 —— 主詞若已由型別固定，
   * 「依主詞分層」量到的其實是型別本身。這裡把它釘住，免得日後把成員一路填成單向、
   * 使該維度悄悄退化成 [EventType] 的化身而沒人發現。
   */
  @Test
  fun `雙向型別必須涵蓋每一個生命領域`() {
    val bidirectional = EventType.entries.filter { it.fixedAgency == null }
    assertTrue(bidirectional.size >= 15, "雙向型別只剩 ${bidirectional.size} 個")

    // OTHERS 不算 —— 它是無領域可歸者的收容所，不能拿來充數
    val covered = bidirectional.filter { it != EventType.OTHERS }.map { it.category }.toSet()
    assertEquals(EventCategory.entries.toSet() - EventCategory.OTHERS, covered,
                 "有生命領域完全沒有雙向型別，該領域的主詞分層必然退化")
  }
}
