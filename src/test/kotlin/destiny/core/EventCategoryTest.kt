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
}
