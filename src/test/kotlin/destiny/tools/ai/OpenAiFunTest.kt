/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai

import com.jayway.jsonpath.JsonPath
import destiny.tools.KotlinLogging
import destiny.tools.ai.OpenAi.FunctionDeclaration
import destiny.tools.ai.OpenAi.FunctionDeclaration.Function
import destiny.tools.ai.OpenAi.FunctionDeclaration.Function.Parameters
import destiny.tools.ai.OpenAi.FunctionDeclaration.Function.Parameters.Argument
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OpenAiFunTest {

  private val logger = KotlinLogging.logger { }

  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    // to ignore unknown keys
    ignoreUnknownKeys = true
  }

  @Nested
  inner class FunctionSerializationTests {
    @Test
    fun weather() {
      val currentWeather = FunctionDeclaration(
        type = "function",
        function = Function(
          name = "get_current_weather",
          description = "Get the current weather",
          parameters = Parameters(
            type = "object",
            properties = mapOf(
              "location" to Argument(
                type = "string",
                description = "The city and state, e.g. San Francisco, CA"
              ),
              "format" to Argument(
                type = "string",
                enum = listOf("celsius", "fahrenheit"),
                description = "The temperature unit to use. Infer this from the users location."
              )
            ), required = listOf("location", "format")
          )
        )
      )

      Json.encodeToString(currentWeather).also { raw ->
        logger.info { raw }
        val ctx = JsonPath.parse(raw)
        assertEquals("function", ctx.read("$.type", String::class.java))
        assertEquals("get_current_weather", ctx.read("$.function.name", String::class.java))
        assertEquals("object", ctx.read("$.function.parameters.type", String::class.java))
        assertEquals("string", ctx.read("$.function.parameters.properties.location.type", String::class.java))
        assertEquals(listOf("celsius", "fahrenheit"), ctx.read("$.function.parameters.properties.format.enum", List::class.java))
        assertEquals(listOf("location", "format"), ctx.read("$.function.parameters.required", List::class.java))
      }
    }
  }

  @Nested
  inner class DecodeReplyTest {

    @Test
    fun errorDecode() {
      val raw = """
      [{
        "error": {
          "code": 400,
          "message": "Please ensure that multiturn requests alternate between user and model.",
          "status": "INVALID_ARGUMENT"
        }
      }
      ]
    """.trimIndent()
      val content = json.decodeFromString<List<Gemini.ResponseContainer.ErrorContainer>>(raw)
      logger.info { "content = $content" }
      content[0].error.also { error ->
        assertNotNull(error)
        assertEquals(400, error.code)
        assertEquals("Please ensure that multiturn requests alternate between user and model.", error.message)
        assertEquals("INVALID_ARGUMENT", error.status)
      }
    }

    @Test
    fun normalReply() {
      val raw = """
        {
          "id": "chatcmpl-8bN5aJO2IWtlxMcHblpnKzwcIfPeL",
          "object": "chat.completion",
          "created": 1703917582,
          "model": "gpt-3.5-turbo-0613",
          "choices": [
            {
              "index": 0,
              "message": {
                "role": "assistant",
                "content": "早安！一加一等於二。"
              },
              "logprobs": null,
              "finish_reason": "stop"
            }
          ],
          "usage": {
            "prompt_tokens": 24,
            "completion_tokens": 12,
            "total_tokens": 36
          },
          "system_fingerprint": null
        }
      """.trimIndent()

      val result: OpenAi.Result = json.decodeFromString(raw)
      assertNotNull(result)
      assertEquals("chatcmpl-8bN5aJO2IWtlxMcHblpnKzwcIfPeL", result.id)
      assertEquals("chat.completion", result.`object`)
      assertEquals(1703917582, result.created)
      assertEquals("gpt-3.5-turbo-0613", result.model)
      assertEquals(1, result.choices.size)
      assertEquals(24, result.usage.promptTokens)
      assertEquals(12, result.usage.completionTokens)
      assertEquals(36, result.usage.totalTokens)
    }
  }

}
