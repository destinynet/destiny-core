/**
 * Created by smallufo on 2023-12-30.
 */
package destiny.tools.ai

import com.jayway.jsonpath.JsonPath
import destiny.tools.ai.OpenAiFun.Function.Parameters.Argument
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenAiFunTest {

  private val logger = KotlinLogging.logger { }

  @OptIn(ExperimentalSerializationApi::class)
  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    // to ignore unknown keys
    ignoreUnknownKeys = true
  }

  @Test
  fun weather() {
    val currentWeather = OpenAiFun(
      type = "function",
      function = OpenAiFun.Function(
        name = "get_current_weather",
        description = "Get the current weather",
        parameters = OpenAiFun.Function.Parameters(
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

    json.encodeToString(currentWeather).also { raw ->
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
