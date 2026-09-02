package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals

class EventRoleTest {

  /** 值域必須恰為兩個 —— 第三個 role 已在設計階段否決（「主角是身邊的人」另立 Protagonist 一軸）。 */
  @Test
  fun `EventRole 恰有兩個值`() {
    assertEquals(setOf("INITIATOR", "RECIPIENT"), EventRole.entries.map { it.name }.toSet())
  }

  /** 與判讀側 Agency 的對應必須成立 —— 兩者語意相同，分型別只為了值域。 */
  @Test
  fun `與 Agency 的對應`() {
    assertEquals(Agency.ACTIVE, EventRole.INITIATOR.toAgency())
    assertEquals(Agency.PASSIVE, EventRole.RECIPIENT.toAgency())
  }
}
