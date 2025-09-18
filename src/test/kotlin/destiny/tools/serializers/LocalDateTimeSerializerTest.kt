/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LocalDateTimeSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json { encodeDefaults = false }


  @Nested
  inner class SerializeTest {
    @Test
    fun minute() {
      val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50)
      val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
      logger.info { serialized }
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

      assertEquals(dateTime, deserialized)
    }

    @Test
    fun second() {
      val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50, 30)
      val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
      logger.info { serialized }
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

      assertEquals(dateTime, deserialized)
    }

    @Test
    fun nanoSecond() {
      val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50, 30, 123456789)
      val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
      logger.info { serialized }
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

      assertEquals(dateTime, deserialized)
    }
  }



  @Nested
  inner class DeserializeStringTest {
    @Test
    fun `minute precision`() {
      val jsonString = "\"2023-10-24T01:50\""
      val expected = LocalDateTime.of(2023, 10, 24, 1, 50)
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, jsonString)
      assertEquals(expected, deserialized)
    }

    @Test
    fun `second precision`() {
      val jsonString = "\"2023-10-24T01:50:30\""
      val expected = LocalDateTime.of(2023, 10, 24, 1, 50, 30)
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, jsonString)
      assertEquals(expected, deserialized)
    }

    @Test
    fun `nano precision`() {
      val jsonString = "\"2023-10-24T01:50:30.123456789\""
      val expected = LocalDateTime.of(2023, 10, 24, 1, 50, 30, 123456789)
      val deserialized = json.decodeFromString(LocalDateTimeSerializer, jsonString)
      assertEquals(expected, deserialized)
    }

    @Test
    fun `invalid string format throws exception`() {
      val invalidJsonString = "\"2024-07-22 15:30:45\"" // 無效的日期時間格式

      val exception = assertFailsWith<IllegalStateException> {
        json.decodeFromString(LocalDateTimeSerializer, invalidJsonString)
      }
      assertTrue(exception.message!!.contains("Invalid LocalDateTime string format"))
      assertTrue(exception.cause is DateTimeParseException)
    }
  }


  @Nested
  inner class DeserializeJsonTest {
    @Test
    fun `complete precision`() {
      val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22,
                "hour": 15,
                "minute": 30,
                "second": 45,
                "nano": 123456789
            }
        """.trimIndent()
      val expectedLocalDateTime = LocalDateTime.of(2025, 7, 22, 15, 30, 45, 123456789)
      val actualLocalDateTime = json.decodeFromString(LocalDateTimeSerializer, jsonObjectString)
      assertEquals(expectedLocalDateTime, actualLocalDateTime)
    }

    @Test
    fun `minute precision`() {
      val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22,
                "hour": 15,
                "minute": 30
            }
        """.trimIndent()
      val expectedLocalDateTime = LocalDateTime.of(2025, 7, 22, 15, 30)
      val actualLocalDateTime = json.decodeFromString(LocalDateTimeSerializer, jsonObjectString)
      assertEquals(expectedLocalDateTime, actualLocalDateTime)
    }

    @Test
    fun `with missing fields throws exception`() {
      val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22,
                "hour": 15
            }
        """.trimIndent() // 缺少 minute, second, nano

      val exception = assertFailsWith<IllegalStateException> {
        json.decodeFromString(LocalDateTimeSerializer, jsonObjectString)
      }

      assertTrue(exception.message!!.contains("LocalTime object must contain 'hour' and 'minute' fields"))
    }

    @Test
    fun `with invalid values throws exception`() {
      val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22,
                "hour": 25,
                "minute": 30,
                "second": 45
            }
        """.trimIndent() // hour 無效

      val exception = assertFailsWith<IllegalStateException> {
        json.decodeFromString(LocalDateTimeSerializer, jsonObjectString)
      }
      assertTrue(exception.message!!.contains("Invalid LocalTime object format"))
      assertTrue(exception.cause is DateTimeException)
    }
  }











  @Test
  fun `deserialize unsupported format throws exception`() {
    val jsonUnsupported = "12345" // 既不是字串也不是物件
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateTimeSerializer, jsonUnsupported)
    }
    assertTrue(exception.message!!.contains("Unsupported LocalDateTime format"))
  }
}
