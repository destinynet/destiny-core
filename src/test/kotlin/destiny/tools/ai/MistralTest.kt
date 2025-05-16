/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai

import com.jayway.jsonpath.JsonPath
import destiny.tools.KotlinLogging
import destiny.tools.ai.model.BirthDataReply
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class MistralTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
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
      val chatModel = Mistral.ChatModel(
        "mistral-small-latest",
        listOf(OpenAi.Message("user", "test message", null, null, emptyList()))
      )
      json.encodeToString(chatModel).also { raw ->
        logger.info { raw }
        assertEquals("text", JsonPath.read(raw, "$.response_format.type"))
      }
    }

    @Test
    fun jsonSchema() {
      val chatModel = Mistral.ChatModel(
        "mistral-small-latest", listOf(OpenAi.Message("user", "test message", null, null, emptyList())),
        jsonSchemaSpec = BirthDataReply::class.toJsonSchema("BirthDataReply", "reply of a horoscope chart")
      )
      json.encodeToString(chatModel).also { raw ->
        logger.info { raw }
        assertEquals("json_schema", JsonPath.read(raw, "$.response_format.type"))
      }
    }
  }
}
