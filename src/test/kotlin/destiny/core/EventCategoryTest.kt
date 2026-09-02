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

  /**
   * ResourceBundle 的 fallback 讓 [testEnums] 看不見漏翻譯，故改比對**檔案本身**的 key 集合 ——
   * 同 [SituationBundleParityTest] 對 [Situation] 所做的事。
   */
  @Test
  fun `每個語系都有翻譯`() {
    assertBundleParity("EventCategory", EventCategory.entries.map { it.name }, "destiny/core")
  }
}
