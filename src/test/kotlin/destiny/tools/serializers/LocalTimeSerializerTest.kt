/**
 * Created by smallufo on 2025-07-22.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import java.time.DateTimeException
import java.time.LocalTime
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LocalTimeSerializerTest {

  private val json = Json { prettyPrint = true }

  @Test
  fun `serialize LocalTime to ISO_LOCAL_TIME string`() {
    val localTime = LocalTime.of(15, 30, 45, 123456789)
    val expectedJson = "\"15:30:45.123456789\"" // 預期序列化後的 JSON 字串
    val actualJson = json.encodeToString(LocalTimeSerializer, localTime)
    assertEquals(expectedJson, actualJson)
  }

  @Test
  fun `deserialize ISO_LOCAL_TIME string to LocalTime`() {
    val jsonString = "\"09:05:01.987654321\""
    val expectedLocalTime = LocalTime.of(9, 5, 1, 987654321)
    val actualLocalTime = json.decodeFromString(LocalTimeSerializer, jsonString)
    assertEquals(expectedLocalTime, actualLocalTime)
  }

  @Test
  fun `deserialize object format to LocalTime`() {
    val jsonObjectString = """
            {
                "hour": 10,
                "minute": 20,
                "second": 30,
                "nano": 400000000
            }
        """.trimIndent()
    val expectedLocalTime = LocalTime.of(10, 20, 30, 400000000)
    val actualLocalTime = json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    assertEquals(expectedLocalTime, actualLocalTime)
  }

  @Test
  fun `deserialize object format with optional fields to LocalTime`() {
    val jsonObjectString = """
            {
                "hour": 11,
                "minute": 22
            }
        """.trimIndent()
    val expectedLocalTime = LocalTime.of(11, 22, 0, 0) // second 和 nano 應為預設值 0
    val actualLocalTime = json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    assertEquals(expectedLocalTime, actualLocalTime)
  }

  @Test
  fun `deserialize object format with only hour and minute`() {
    val jsonObjectString = """
            {
                "hour": 14,
                "minute": 59
            }
        """.trimIndent()
    val expectedLocalTime = LocalTime.of(14, 59)
    val actualLocalTime = json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    assertEquals(expectedLocalTime, actualLocalTime)
  }

  @Test
  fun `deserialize invalid string format throws exception`() {
    val invalidJsonString = "\"25:00:00\"" // 無效的小時
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalTimeSerializer, invalidJsonString)
    }
    assertTrue(exception.message!!.contains("Invalid LocalTime string format"))
    assertTrue(exception.cause is DateTimeParseException)
  }

  @Test
  fun `deserialize object missing hour field throws exception`() {
    val jsonObjectString = """
            {
                "minute": 30
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("LocalTime object must contain 'hour' and 'minute' fields"))
  }

  @Test
  fun `deserialize object missing minute field throws exception`() {
    val jsonObjectString = """
            {
                "hour": 10
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("LocalTime object must contain 'hour' and 'minute' fields"))
  }

  @Test
  fun `deserialize object with invalid hour value throws exception`() {
    val jsonObjectString = """
            {
                "hour": 25,
                "minute": 0
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalTimeSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("Invalid LocalTime object format"))
    assertTrue(exception.cause is DateTimeException)
  }

  @Test
  fun `deserialize unsupported format throws exception`() {
    val jsonUnsupported = "[]" // 既不是字串也不是物件
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalTimeSerializer, jsonUnsupported)
    }
    assertTrue(exception.message!!.contains("Unsupported LocalTime format"))
  }
}
