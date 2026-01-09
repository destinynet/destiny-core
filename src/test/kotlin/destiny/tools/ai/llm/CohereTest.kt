/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai.llm

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CohereTest {

  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    ignoreUnknownKeys = true
  }

  private val logger = KotlinLogging.logger { }

  @Nested
  inner class DeserializeTest {

    @Test
    fun reqWithFunCall() {
      val raw = """
        {
           "model":"command-r-plus-08-2024",
           "tools":[
              {
                 "type":"function",
                 "function":{
                    "name":"get_current_weather",
                    "description":"Get the current weather",
                    "parameters":{
                       "type":"object",
                       "properties":{
                          "location":{
                             "description":"The city and state, e.g. San Francisco, CA",
                             "type":"string"
                          },
                          "format":{
                             "description":"The temperature unit to use. C for Celsius, F for Fahrenheit",
                             "type":"string"
                          }
                       },
                       "required":[
                          "location",
                          "format"
                       ]
                    }
                 }
              }
           ],
           "messages":[
              {
                 "role":"user",
                 "content" : [
                   {
                     "type": "text",
                     "text" : "What is the weather like today in New York and Taipei ?\n\nWith function calls if applicable : get_current_weather"
                   }
                 ]
              }
           ]
        }
      """.trimIndent()
      json.decodeFromString<Cohere.Request>(raw).also { req ->
        logger.info { req }
        assertEquals(1, req.tools.size)
        req.tools[0].function.also { f ->
          assertEquals("get_current_weather", f.name)
          assertEquals(2, f.parameters.properties.size)
          f.parameters.properties["location"].also { para ->
            assertNotNull(para)
            assertEquals("string", para.type)
            f.parameters.required.contains("location")
          }

          f.parameters.properties["format"].also { para ->
            assertNotNull(para)
            assertEquals("string", para.type)
            f.parameters.required.contains("format")
          }
        }
        assertEquals(1, req.messages.size)
      }
    }

    @Test
    fun resWithFunInvocation() {
      val raw = """
        {
           "id":"8e9677a4-56e1-4926-a217-2baffbcedf2d",
           "message":{
              "role":"assistant",
              "tool_plan":"I will run two concurrent searches for the weather in New York and Taipei and relay this information to the user.",
              "tool_calls":[
                 {
                    "id":"get_current_weather_scz5wyskxyaw",
                    "type":"function",
                    "function":{
                       "name":"get_current_weather",
                       "arguments":"{\"format\":\"F\",\"location\":\"New York\"}"
                    }
                 },
                 {
                    "id":"get_current_weather_bx4jv9yfje5y",
                    "type":"function",
                    "function":{
                       "name":"get_current_weather",
                       "arguments":"{\"format\":\"F\",\"location\":\"Taipei\"}"
                    }
                 }
              ]
           },
           "finish_reason":"TOOL_CALL",
           "usage":{
              "billed_units":{
                 "input_tokens":64,
                 "output_tokens":52
              },
              "tokens":{
                 "input_tokens":947,
                 "output_tokens":113
              }
           }
        }
      """.trimIndent()
      json.decodeFromString<Cohere.Response>(raw).also { res ->
        logger.info { res }
        res.message.also { message ->
          assertEquals("assistant" , message.role)
          assertEquals("I will run two concurrent searches for the weather in New York and Taipei and relay this information to the user.", message.toolPlan)
          message.toolCalls.also { toolCalls ->
            assertNotNull(toolCalls)
            assertEquals(2, toolCalls.size)
            toolCalls[0].also { toolCall ->
              assertEquals("get_current_weather_scz5wyskxyaw", toolCall.id)
              toolCall.function.also { f ->
                assertEquals("get_current_weather", f.name)
              }
            }

            toolCalls[1].also { toolCall ->
              assertEquals("get_current_weather_bx4jv9yfje5y", toolCall.id)
              toolCall.function.also { f ->
                assertEquals("get_current_weather", f.name)
              }
            }
          }

        }
      }
    }
  }
}
