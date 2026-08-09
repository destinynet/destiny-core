/**
 * Created by smallufo on 2025-08-10.
 */
package destiny.core

import kotlin.test.Test

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
}
