/**
 * Created by smallufo on 2025-12-18.
 */
package destiny.tools.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnyComplexSerializerTest {

  @Serializable
  data class TestData(
    val value: @Serializable(with = AnyComplexSerializer::class) Any
  )

  private val json = Json {
    serializersModule = SerializersModule {
      contextual(AnyComplexSerializer)
    }
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  @Test
  fun `test serialize and deserialize primitive types`() {
    // String
    val stringData = TestData("Hello World")
    val stringJson = json.encodeToString(TestData.serializer(), stringData)
    val stringResult = json.decodeFromString(TestData.serializer(), stringJson)
    assertEquals("Hello World", stringResult.value)

    // Int
    val intData = TestData(42)
    val intJson = json.encodeToString(TestData.serializer(), intData)
    val intResult = json.decodeFromString(TestData.serializer(), intJson)
    assertEquals(42, intResult.value)

    // Long
    val longData = TestData(9999999999L)
    val longJson = json.encodeToString(TestData.serializer(), longData)
    val longResult = json.decodeFromString(TestData.serializer(), longJson)
    assertEquals(9999999999L, longResult.value)

    // Double
    val doubleData = TestData(3.14159)
    val doubleJson = json.encodeToString(TestData.serializer(), doubleData)
    val doubleResult = json.decodeFromString(TestData.serializer(), doubleJson)
    assertEquals(3.14159, doubleResult.value)

    // Boolean
    val boolData = TestData(true)
    val boolJson = json.encodeToString(TestData.serializer(), boolData)
    val boolResult = json.decodeFromString(TestData.serializer(), boolJson)
    assertEquals(true, boolResult.value)
  }

  @Test
  fun `test serialize and deserialize LocalDateTime`() {
    val dateTime = LocalDateTime.of(2025, 12, 18, 14, 30, 0)
    val data = TestData(dateTime)

    val jsonString = json.encodeToString(TestData.serializer(), data)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    // LocalDateTime is serialized as ISO string and deserialized back as String
    assertTrue(result.value is String)
    assertTrue((result.value as String).startsWith("2025-12-18T14:30"))
  }

  @Test
  fun `test serialize and deserialize simple Map`() {
    val mapData = TestData(mapOf("name" to "Alice", "age" to 30))

    val jsonString = json.encodeToString(TestData.serializer(), mapData)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is Map<*, *>)
    val resultMap = result.value as Map<*, *>
    assertEquals("Alice", resultMap["name"])
    assertEquals(30, resultMap["age"])
  }

  @Test
  fun `test serialize and deserialize simple List`() {
    val listData = TestData(listOf("apple", "banana", "cherry"))

    val jsonString = json.encodeToString(TestData.serializer(), listData)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is List<*>)
    val resultList = result.value as List<*>
    assertEquals(3, resultList.size)
    assertEquals("apple", resultList[0])
    assertEquals("banana", resultList[1])
    assertEquals("cherry", resultList[2])
  }

  @Test
  fun `test serialize and deserialize nested Map`() {
    val nestedMap = mapOf(
      "user" to mapOf(
        "name" to "Bob",
        "age" to 25,
        "active" to true
      ),
      "score" to 95.5
    )
    val data = TestData(nestedMap)

    val jsonString = json.encodeToString(TestData.serializer(), data)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is Map<*, *>)
    val resultMap = result.value as Map<*, *>

    assertTrue(resultMap["user"] is Map<*, *>)
    val userMap = resultMap["user"] as Map<*, *>
    assertEquals("Bob", userMap["name"])
    assertEquals(25, userMap["age"])
    assertEquals(true, userMap["active"])
    assertEquals(95.5, resultMap["score"])
  }

  @Test
  fun `test serialize and deserialize nested List`() {
    val nestedList = listOf(
      listOf(1, 2, 3),
      listOf(4, 5, 6),
      listOf(7, 8, 9)
    )
    val data = TestData(nestedList)

    val jsonString = json.encodeToString(TestData.serializer(), data)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is List<*>)
    val resultList = result.value as List<*>
    assertEquals(3, resultList.size)

    assertTrue(resultList[0] is List<*>)
    val firstRow = resultList[0] as List<*>
    assertEquals(listOf(1, 2, 3), firstRow)
  }

  @Test
  fun `test serialize and deserialize Map with List values`() {
    val complexMap = mapOf(
      "numbers" to listOf(1, 2, 3, 4, 5),
      "fruits" to listOf("apple", "banana"),
      "metadata" to mapOf(
        "version" to 1,
        "tags" to listOf("important", "urgent")
      )
    )
    val data = TestData(complexMap)

    val jsonString = json.encodeToString(TestData.serializer(), data)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is Map<*, *>)
    val resultMap = result.value as Map<*, *>

    val numbers = resultMap["numbers"] as List<*>
    assertEquals(5, numbers.size)
    assertEquals(1, numbers[0])

    val fruits = resultMap["fruits"] as List<*>
    assertEquals(2, fruits.size)
    assertEquals("apple", fruits[0])

    val metadata = resultMap["metadata"] as Map<*, *>
    assertEquals(1, metadata["version"])

    val tags = metadata["tags"] as List<*>
    assertEquals("important", tags[0])
    assertEquals("urgent", tags[1])
  }

  @Test
  fun `test serialize and deserialize mixed types`() {
    val mixedData = mapOf(
      "string" to "hello",
      "int" to 42,
      "double" to 3.14,
      "boolean" to true,
      "list" to listOf(1, "two", 3.0),
      "map" to mapOf("nested" to "value"),
      "dateTime" to LocalDateTime.of(2025, 1, 1, 0, 0)
    )
    val data = TestData(mixedData)

    val jsonString = json.encodeToString(TestData.serializer(), data)
    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertTrue(result.value is Map<*, *>)
    val resultMap = result.value as Map<*, *>

    assertEquals("hello", resultMap["string"])
    assertEquals(42, resultMap["int"])
    assertEquals(3.14, resultMap["double"])
    assertEquals(true, resultMap["boolean"])

    assertTrue(resultMap["list"] is List<*>)
    val list = resultMap["list"] as List<*>
    assertEquals(1, list[0])
    assertEquals("two", list[1])
    assertEquals(3.0, list[2])

    assertTrue(resultMap["map"] is Map<*, *>)
    assertEquals("value", (resultMap["map"] as Map<*, *>)["nested"])

    // DateTime is serialized as ISO string
    assertTrue(resultMap["dateTime"] is String)
    assertTrue((resultMap["dateTime"] as String).startsWith("2025-01-01T00:00"))
  }

  @Test
  fun `test int vs long deserialization`() {
    // Small numbers should be Int
    val smallNum = TestData(42)
    val smallJson = json.encodeToString(TestData.serializer(), smallNum)
    val smallResult = json.decodeFromString(TestData.serializer(), smallJson)
    assertTrue(smallResult.value is Int)

    // Large numbers should be Long
    val largeNum = TestData(9999999999L)
    val largeJson = json.encodeToString(TestData.serializer(), largeNum)
    val largeResult = json.decodeFromString(TestData.serializer(), largeJson)
    assertTrue(largeResult.value is Long)
  }
}
