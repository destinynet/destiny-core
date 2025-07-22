/**
 * Created by smallufo on 2025-07-22.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import java.time.DateTimeException
import java.time.YearMonth
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class YearMonthSerializerTest {
  private val json = Json { prettyPrint = true }

  @Test
  fun `serialize YearMonth to YYYY-MM string`() {
    val yearMonth = YearMonth.of(2023, 11)
    val expectedJson = "\"2023-11\"" // 預期序列化後的 JSON 字串
    val actualJson = json.encodeToString(YearMonthSerializer, yearMonth)
    assertEquals(expectedJson, actualJson)
  }

  @Test
  fun `deserialize YYYY-MM string to YearMonth`() {
    val jsonString = "\"2024-03\""
    val expectedYearMonth = YearMonth.of(2024, 3)
    val actualYearMonth = json.decodeFromString(YearMonthSerializer, jsonString)
    assertEquals(expectedYearMonth, actualYearMonth)
  }

  @Test
  fun `deserialize object format to YearMonth`() {
    val jsonObjectString = """
            {
                "year": 2025,
                "month": 7
            }
        """.trimIndent()
    val expectedYearMonth = YearMonth.of(2025, 7)
    val actualYearMonth = json.decodeFromString(YearMonthSerializer, jsonObjectString)
    assertEquals(expectedYearMonth, actualYearMonth)
  }

  @Test
  fun `deserialize object format with extra fields to YearMonth`() {
    val jsonObjectString = """
            {
                "year": 2025,
                "month": 7,
                "day": 22
            }
        """.trimIndent()
    val expectedYearMonth = YearMonth.of(2025, 7)
    val actualYearMonth = json.decodeFromString(YearMonthSerializer, jsonObjectString)
    assertEquals(expectedYearMonth, actualYearMonth)
  }

  @Test
  fun `deserialize invalid string format throws exception`() {
    val invalidJsonString = "\"2024/03\"" // 無效的日期格式

    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(YearMonthSerializer, invalidJsonString)
    }
    assertTrue(exception.message!!.contains("Invalid YearMonth string format"))
    assertTrue(exception.cause is DateTimeParseException)
  }

  @Test
  fun `deserialize object missing year field throws exception`() {
    val jsonObjectString = """
            {
                "month": 12
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(YearMonthSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("YearMonth object must contain 'year' and 'month' fields"))
  }

  @Test
  fun `deserialize object missing month field throws exception`() {
    val jsonObjectString = """
            {
                "year": 2024
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(YearMonthSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("YearMonth object must contain 'year' and 'month' fields"))
  }

  @Test
  fun `deserialize object with invalid month value throws exception`() {
    val jsonObjectString = """
            {
                "year": 2024,
                "month": 13
            }
        """.trimIndent()
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(YearMonthSerializer, jsonObjectString)
    }
    assertTrue(exception.message!!.contains("Invalid YearMonth object format"))
    assertTrue(exception.cause is DateTimeException)
  }

  @Test
  fun `deserialize unsupported format throws exception`() {
    val jsonUnsupported = "12345" // 既不是字串也不是物件
    val exception = assertFailsWith<IllegalStateException> {
      json.decodeFromString(YearMonthSerializer, jsonUnsupported)
    }
    assertTrue(exception.message!!.contains("Unsupported YearMonth format"))
  }
}
