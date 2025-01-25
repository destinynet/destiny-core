/**
 * Created by smallufo on 2025-01-25.
 */package destiny.tools.serializers
import destiny.core.Gender
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GenderSerializerTest {
 private val json = Json {
  ignoreUnknownKeys = true
  isLenient = true
 }

 @Test
 fun `test serialize gender`() {
  assertEquals("\"M\"", json.encodeToString(Gender.男))
  assertEquals("\"F\"", json.encodeToString(Gender.女))
 }

 @Test
 fun `test deserialize gender - uppercase`() {
  assertEquals(Gender.男, json.decodeFromString("\"M\""))
  assertEquals(Gender.女, json.decodeFromString("\"F\""))
 }

 @Test
 fun `test deserialize gender - lowercase`() {
  assertEquals(Gender.男, json.decodeFromString("\"m\""))
  assertEquals(Gender.女, json.decodeFromString("\"f\""))
 }

 @Test
 fun `test deserialize invalid gender`() {
  assertFailsWith<IllegalArgumentException> {
   json.decodeFromString<Gender>("\"X\"")
  }
 }
}
