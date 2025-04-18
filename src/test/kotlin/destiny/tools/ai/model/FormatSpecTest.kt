/**
 * Created by smallufo on 2025-04-19.
 */
package destiny.tools.ai.model

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class FormatSpecTest {

  @Test
  fun validSerializableClass() {
    val formatSpec = FormatSpec.of<Advice>("Advice", "This is some advice")

    // 確認 serializer 和 schema 都正確
    assertNotNull(formatSpec)
    assertEquals(Advice.serializer(), formatSpec.serializer)
    assertEquals("Advice", formatSpec.jsonSchema.name)
    assertEquals("This is some advice", formatSpec.jsonSchema.description)
  }

  @Test
  fun nonSerializableClass() {
    class NonSerializableClass(val name: String)

    assertFailsWith<IllegalStateException> {
      FormatSpec.of<NonSerializableClass>("NonSerializable", "Should fail")
    }
  }

  @Test
  fun localDateTime() {
    assertFailsWith<IllegalStateException> {
      FormatSpec.of<LocalDateTime>("DateTime", "This should fail for LocalDateTime")
    }
  }

  @Test
  fun unitTest() {
    assertFailsWith<IllegalArgumentException> {
      FormatSpec.of<Unit>("Unit", "Unit type should fail")
    }
  }
}
