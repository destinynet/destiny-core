/**
 * Created by smallufo on 2025-08-10.
 */
package destiny.core

import kotlin.test.Test

class EventCategoryTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(EventCategory::class, true)
  }

  /** 同 [EventTypeTest]：ResourceBundle 的 fallback 讓 [testEnums] 看不見漏翻譯 */
  @Test
  fun `每個語系都有翻譯`() {
    assertBundleParity("EventCategory", EventCategory.entries.map { it.name }, "destiny/core")
  }
}
