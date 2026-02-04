/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai.llm

import com.jayway.jsonpath.JsonPath
import destiny.tools.KotlinLogging
import destiny.tools.ai.llm.OpenAi.Response.NormalResponse
import destiny.tools.ai.model.SynastryReply
import destiny.tools.ai.toJsonSchema
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.*

class OpenAiTest {

  private val logger = KotlinLogging.logger {  }

  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    // to ignore unknown keys
    ignoreUnknownKeys = true
  }

  @Nested
  inner class ResponseFormatTest {

    @Test
    fun text() {
      val chatModel = OpenAi.ChatModel(
        listOf(OpenAi.Message.TextContent("user", "test message")),
        null, "gpt-4o", OpenAi.OpenAiOptions(0.9, null, null), null, emptyList(), null
      )
      json.encodeToString(chatModel).also { raw ->
        logger.info { raw }
        assertEquals("text", JsonPath.read(raw, "$.response_format.type"))
      }
    }

    @Test
    fun jsonSchema() {
      val chatModel = OpenAi.ChatModel(
        listOf(OpenAi.Message.TextContent("user", "test message")),
        null, "gpt-4o", OpenAi.OpenAiOptions(0.9, null, null), null, emptyList(), SynastryReply::class.toJsonSchema("SynastryReply", "reply of a synastry chart")
      )
      json.encodeToString(chatModel).also { raw ->
        logger.info { raw }
        assertEquals("json_schema", JsonPath.read(raw, "$.response_format.type"))
      }
    }
  }


  @Nested
  inner class FunCallDeserializeTest {
    @Test
    fun horoscopeForecastYear() {
      val raw = """
        {
          "id": "chatcmpl-AHvUTLfLpfm0iJsCxsS9EaJalXUJs",
          "object": "chat.completion",
          "created": 1728835933,
          "model": "gpt-3.5-turbo-1106",
          "choices": [
            {
              "index": 0,
              "message": {
                "role": "assistant",
                "content": null,
                "tool_calls": [
                  {
                    "id": "call_7orqgQWi2a8L414l2KRQd5Se",
                    "type": "function",
                    "function": {
                      "name": "get_horoscope_forecast_year",
                      "arguments": "{\"birthYear\": 2010, \"birthMonth\": 1, \"birthDay\": 1, \"birthHour\": 18, \"birthMinute\": 0, \"birthLat\": 25.03903, \"birthLng\": 121.517668, \"gender\": \"M\", \"forecastYear\": 2027}"
                    }
                  },
                  {
                    "id": "call_QP0ed3ZV3NJDPupEH3a8njmC",
                    "type": "function",
                    "function": {
                      "name": "get_horoscope_forecast_year",
                      "arguments": "{\"birthYear\": 2010, \"birthMonth\": 1, \"birthDay\": 1, \"birthHour\": 18, \"birthMinute\": 0, \"birthLat\": 25.03903, \"birthLng\": 121.517668, \"gender\": \"M\", \"forecastYear\": 2028}"
                    }
                  }
                ],
                "refusal": null
              },
              "logprobs": null,
              "finish_reason": "tool_calls"
            }
          ],
          "usage": {
            "prompt_tokens": 4045,
            "completion_tokens": 147,
            "total_tokens": 4192,
            "prompt_tokens_details": {
              "cached_tokens": 0
            },
            "completion_tokens_details": {
              "reasoning_tokens": 0
            }
          },
          "system_fingerprint": "fp_c0b1db30f1"
        }
      """.trimIndent()

      json.decodeFromString<NormalResponse>(raw).also { result: NormalResponse ->
        assertEquals(1 , result.choices.size)
        val message = result.choices[0].message as OpenAi.Message.TextContent
        assertNull(message.content)
        message.toolCalls.also {
          assertNotNull(it)
          assertEquals(2 , it.size)
          it.forEach { toolCall: OpenAi.Message.ToolCall ->
            assertEquals("get_horoscope_forecast_year", toolCall.function.name)
            assertTrue { toolCall.function.arguments.isNotBlank() }
          }
        }
      }
    }

  }
}
