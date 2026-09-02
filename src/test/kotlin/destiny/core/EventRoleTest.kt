/**
 * Created by smallufo on 2026-09-02.
 */
package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals

class EventRoleTest {

  /**
   * 值域必須恰為兩個 —— 第三個 role 已在設計階段否決（「主角是身邊的人」另立 Protagonist 一軸）。
   *
   * ⚠️ 這條是**唯一**的守門員。先前還有 `toAgency()` 的窮舉 `when` 在擋
   * （多一個值就編譯失敗），但那個函式已刻意移除（理由見 [EventRole] 的 KDoc），
   * 那道防線隨之消失。⇒ 刪掉這條測試，加第三個值就再也不會有人被擋下來。
   */
  @Test
  fun `EventRole 恰有兩個值`() {
    assertEquals(setOf("INITIATOR", "RECIPIENT"), EventRole.entries.map { it.name }.toSet())
  }
}
