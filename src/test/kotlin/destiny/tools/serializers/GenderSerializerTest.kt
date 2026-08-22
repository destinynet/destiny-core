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
    assertEquals("\"M\"", Json.encodeToString(GenderSerializer, Gender.M))
    assertEquals("\"F\"", Json.encodeToString(GenderSerializer, Gender.F))
  }

  @Test
  fun `test deserialize gender - uppercase`() {
    assertEquals(Gender.M, Json.decodeFromString(GenderSerializer, "\"M\""))
    assertEquals(Gender.F, Json.decodeFromString(GenderSerializer, "\"F\""))
  }

  @Test
  fun `test deserialize gender - lowercase`() {
    assertEquals(Gender.M, Json.decodeFromString(GenderSerializer, "\"m\""))
    assertEquals(Gender.F, Json.decodeFromString(GenderSerializer, "\"f\""))
  }

  /** 2025-12-28 `2b6a2b3b` 改名前的舊 payload 用的是 enum 常數 男/女 */
  @Test
  fun `test deserialize gender - legacy 男女`() {
    assertEquals(Gender.M, Json.decodeFromString(GenderSerializer, "\"男\""))
    assertEquals(Gender.F, Json.decodeFromString(GenderSerializer, "\"女\""))
  }

  @Test
  fun `test deserialize invalid gender`() {
    assertFailsWith<IllegalArgumentException> {
      Json.decodeFromString(GenderSerializer, "\"X\"")
    }
  }
}
