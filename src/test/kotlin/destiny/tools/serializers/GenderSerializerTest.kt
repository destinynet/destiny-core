/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers

import destiny.core.Gender
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GenderSerializerTest {

  @Test
  fun `test serialize gender`() {
    assertEquals("\"M\"", Json.encodeToString(GenderSerializer, Gender.男))
    assertEquals("\"F\"", Json.encodeToString(GenderSerializer, Gender.女))
  }

  @Test
  fun `test deserialize gender - uppercase`() {
    assertEquals(Gender.男, Json.decodeFromString(GenderSerializer, "\"M\""))
    assertEquals(Gender.女, Json.decodeFromString(GenderSerializer, "\"F\""))
  }

  @Test
  fun `test deserialize gender - lowercase`() {
    assertEquals(Gender.男, Json.decodeFromString(GenderSerializer, "\"m\""))
    assertEquals(Gender.女, Json.decodeFromString(GenderSerializer, "\"f\""))
  }

  @Test
  fun `test deserialize invalid gender`() {
    assertFailsWith<IllegalArgumentException> {
      Json.decodeFromString(GenderSerializer, "\"X\"")
    }
  }
}
