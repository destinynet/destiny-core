/**
 * Created by smallufo on 2025-07-23.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LocalDateSerializerTest {
  private val json = Json { prettyPrint = true }

  @Test
  fun `serialize LocalDate to YYYY-MM-DD string`() {
    val localDate = LocalDate.of(2023, 11, 25)
    val expectedJson = "\"2023-11-25\"" // 預期序列化後的 JSON 字串
    val actualJson = json.encodeToString(LocalDateSerializer, localDate)
    assertEquals(expectedJson, actualJson)
  }

  @Test
  fun `deserialize YYYY-MM-DD string to LocalDate`() {
    val jsonString = "\"2024-03-15\""
    val expectedLocalDate = LocalDate.of(2024, 3, 15)
    val actualLocalDate = json.decodeFromString(LocalDateSerializer, jsonString)
    assertEquals(expectedLocalDate, actualLocalDate)
  }

  @Test
  fun `deserialize object format to LocalDate`() {
    val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22
            }
        """.trimIndent()
    val expectedLocalDate = LocalDate.of(2025, 7, 22)
    val actualLocalDate = json.decodeFromString(LocalDateSerializer, jsonObjectString)
    assertEquals(expectedLocalDate, actualLocalDate)
  }

  @Test
  fun `deserialize object format with extra fields to LocalDate`() {
    val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22,
                "hour": 10
            }
        """.trimIndent()
    val expectedLocalDate = LocalDate.of(2025, 7, 22)
    val actualLocalDate = json.decodeFromString(LocalDateSerializer, jsonObjectString)
    assertEquals(expectedLocalDate, actualLocalDate)
  }

  @Test
  fun `deserialize invalid string format throws exception`() {
    val invalidJsonString = "\"2024/03/15\"" // 無效的日期格式

    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, invalidJsonString)
    }
    assertTrue(exception.message!!.contains("Invalid LocalDate string format"))
    assertTrue(exception.cause is DateTimeParseException)
  }

  @Test
  fun `deserialize object missing year field throws exception`() {
    val jsonObjectString = """
            {
                "month": 12,
                "day": 1
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("YearMonth object must contain 'year' and 'month' fields"))
  }

  @Test
  fun `deserialize object missing month field throws exception`() {
    val jsonObjectString = """
            {
                "year": 2024,
                "day": 1
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("YearMonth object must contain 'year' and 'month' fields"))
  }

  @Test
  fun `deserialize object missing day field throws exception`() {
    val jsonObjectString = """
            {
                "year": 2024,
                "month": 12
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("LocalDate object must contain 'year', 'month', and 'day' fields"))
  }

  @Test
  fun `deserialize object with invalid day value throws exception`() {
    val jsonObjectString = """
            {
                "year": 2024,
                "month": 2,
                "day": 30
            }
        """.trimIndent() // 2月沒有30號
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("Invalid LocalDate object format"))
    assertTrue(exception.cause is DateTimeException)
  }

  @Test
  fun `deserialize unsupported format throws exception`() {
    val jsonUnsupported = "12345" // 既不是字串也不是物件
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(LocalDateSerializer, jsonUnsupported)
    }
    assertTrue(exception.message!!.contains("Unsupported LocalDate format"))
  }
}
