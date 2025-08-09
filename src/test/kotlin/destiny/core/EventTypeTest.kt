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
}
