/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClaudeTest {

  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    // to ignore unknown keys
    ignoreUnknownKeys = true
  }

  private val logger = KotlinLogging.logger { }

  @Nested
  inner class DeserializeTest {

    @Test
    fun weather() {
      val raw = """
        {
          "id": "msg_01UbxrHrc72srjNVFeDpFzaz",
          "type": "message",
          "role": "assistant",
          "model": "claude-3-haiku-20240307",
          "content": [
            {
              "type": "text",
              "text": "Okay, let's check the current weather in New York and Taipei:"
            },
            {
              "type": "tool_use",
              "id": "toolu_01WdELdRPyW1hfGLgwL3eLBc",
              "name": "get_current_weather",
              "input": {
                "location": "New York, NY",
                "format": "F"
              }
            }
          ],
          "stop_reason": "tool_use",
          "stop_sequence": null,
          "usage": {
            "input_tokens": 390,
            "output_tokens": 92
          }
        }
      """.trimIndent()
      json.decodeFromString<Claude.Response>(raw).also { response: Claude.Response ->
        logger.info { "claudeResponse = $response" }
        response.contents!!.also { contents ->
          assertEquals(2, contents.size)
          contents.last().also { content ->
            assertEquals("tool_use", content.contentType)
            val toolUse = (content as Claude.Content.ToolUse)
            assertEquals("get_current_weather", toolUse.name)
          }
        }
      }
    }
  }

  @Test
  fun horoscopeForecastYear() {
    val raw = """
      {
        "id": "msg_01NMLtxgG1B4MG6JYEwEwJkY",
        "type": "message",
        "role": "assistant",
        "model": "claude-3-haiku-20240307",
        "content": [
          {
            "type": "text",
            "text": "我已經為您分析了目前星盤的狀況,下面我來預測2027年的運勢:"
          },
          {
            "type": "tool_use",
            "id": "toolu_01XnTA2Sdd9kiP5RiUpGdoer",
            "name": "get_horoscope_forecast_year",
            "input": {
              "birthYear": 2010,
              "birthMonth": 1,
              "birthDay": 1,
              "birthHour": 18,
              "birthMinute": 0,
              "birthLat": 25.03903,
              "birthLng": 121.517668,
              "tzid": "Asia/Taipei",
              "gender": "M",
              "forecastYear": 2027
            }
          }
        ],
        "stop_reason": "tool_use",
        "stop_sequence": null,
        "usage": {
          "input_tokens": 4804,
          "output_tokens": 271
        }
      }
    """.trimIndent()
    json.decodeFromString<Claude.Response>(raw).also { response ->
      logger.info { "claudeResponse = $response" }
      response.contents.also {
        assertNotNull(it)
        it[0].also { content: Claude.Content ->
          assertTrue { content is Claude.Content.Text }
        }
        it[1].also { content: Claude.Content ->
          assertTrue { content is Claude.Content.ToolUse }
          val toolUse = (content as Claude.Content.ToolUse)
          assertEquals("get_horoscope_forecast_year", toolUse.name)
        }
      }
    }

  }
}
