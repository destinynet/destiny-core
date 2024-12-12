/**
 * Created by smallufo on 2023-12-31.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.Gemini.Content.Part.FunctionCall
import destiny.tools.ai.Gemini.FunctionDeclaration
import destiny.tools.ai.Gemini.FunctionDeclaration.Parameters
import destiny.tools.ai.Gemini.FunctionDeclaration.Parameters.Argument
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GeminiTest {

  val logger = KotlinLogging.logger { }

  val json = Json {
    encodeDefaults = true
    prettyPrint = true
    // skip null fields , get rid of null user error
    explicitNulls = false
    // to ignore unknown keys
    ignoreUnknownKeys = true
  }


  @Test
  fun toolsArrayDecode() {
    val raw = """
        [
          {
            "function_declarations": [
              {
                "name": "find_movies",
                "description": "find movie titles currently playing in theaters based on any description, genre, title words, etc.",
                "parameters": {
                  "type": "object",
                  "properties": {
                    "location": {
                      "type": "string",
                      "description": "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"
                    },
                    "description": {
                      "type": "string",
                      "description": "Any kind of description including category or genre, title words, attributes, etc."
                    }
                  },
                  "required": [
                    "description"
                  ]
                }
              },
              {
                "name": "find_theaters",
                "description": "find theaters based on location and optionally movie title which are is currently playing in theaters",
                "parameters": {
                  "type": "object",
                  "properties": {
                    "location": {
                      "type": "string",
                      "description": "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"
                    },
                    "movie": {
                      "type": "string",
                      "description": "Any movie title"
                    }
                  },
                  "required": [
                    "location"
                  ]
                }
              },
              {
                "name": "get_showtimes",
                "description": "Find the start times for movies playing in a specific theater",
                "parameters": {
                  "type": "object",
                  "properties": {
                    "location": {
                      "type": "string",
                      "description": "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"
                    },
                    "movie": {
                      "type": "string",
                      "description": "Any movie title"
                    },
                    "theater": {
                      "type": "string",
                      "description": "Name of the theater"
                    },
                    "date": {
                      "type": "string",
                      "description": "Date for requested showtime"
                    }
                  },
                  "required": [
                    "location",
                    "movie",
                    "theater",
                    "date"
                  ]
                }
              }
            ]
          }
        ]
      """.trimIndent()

    Json.decodeFromString<List<Gemini.Tool>>(raw).also { list ->
      logger.info { list }
      assertEquals(1, list.size)
      val tool = list[0]
      assertEquals(3, tool.functionDeclarations.size)
      assertEquals(setOf("find_movies", "find_theaters", "get_showtimes"),
                   tool.functionDeclarations.map { it.name }.toSet()
      )
    }
  }

  @Nested
  inner class FunctionDeclarationSerializationTests {

    @Test
    fun funCallDeserializeTest() {
      val raw = """
        [
          {
            "candidates": [
              {
                "content": {
                  "role": "model",
                  "parts": [
                    {
                      "functionCall": {
                        "name": "get_current_weather",
                        "args": {
                          "format": "fahrenheit",
                          "location": "New York, NY"
                        }
                      }
                    }
                  ]
                },
                "finishReason": "STOP",
                "safetyRatings": [
                  {
                    "category": "HARM_CATEGORY_HARASSMENT",
                    "probability": "NEGLIGIBLE"
                  },
                  {
                    "category": "HARM_CATEGORY_HATE_SPEECH",
                    "probability": "NEGLIGIBLE"
                  },
                  {
                    "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "probability": "NEGLIGIBLE"
                  },
                  {
                    "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                    "probability": "NEGLIGIBLE"
                  }
                ]
              }
            ],
            "usageMetadata": {
              "promptTokenCount": 11,
              "totalTokenCount": 11
            }
          }
        ]
      """.trimIndent()
      json.decodeFromString<List<Gemini.ResponseContainer.CandidateContainer>>(raw).also { containers ->
        logger.info { containers }
        assertEquals(1 , containers.size)
        val container: Gemini.ResponseContainer.CandidateContainer = containers[0]
        assertEquals(1 , container.candidates.size)
        val candidate = container.candidates[0]
        candidate.content.also { content ->
          assertEquals("model" , content.role)
          assertEquals(1 , content.parts?.size)
          content.parts?.get(0)?.also { part ->
            assertNull(part.text)
            val expected = FunctionCall(
              name = "get_current_weather",
              args = JsonObject(
                mapOf(
                  "format" to JsonPrimitive("fahrenheit"),
                  "location" to JsonPrimitive("New York, NY")
                )
              )
            )
            assertEquals(expected , part.functionCall)
          }
        }
      }
    }

    @Test
    fun findMovies() {
      val raw = """
        {
          "name": "find_movies",
          "description": "find movie titles currently playing in theaters based on any description, genre, title words, etc.",
          "parameters": {
            "type": "object",
            "properties": {
              "location": {
                "type": "string",
                "description": "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"
              },
              "description": {
                "type": "string",
                "description": "Any kind of description including category or genre, title words, attributes, etc."
              }
            },
            "required": [
              "description"
            ]
          }
        }
      """.trimIndent()

      val funDec = Json.decodeFromString<FunctionDeclaration>(raw)
      assertEquals("find_movies", funDec.name)
      assertEquals("find movie titles currently playing in theaters based on any description, genre, title words, etc.", funDec.description)
      val expectedParameters = Parameters(
        type = "object",
        properties = mapOf(
          "location" to Argument("string", "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"),
          "description" to Argument("string", "Any kind of description including category or genre, title words, attributes, etc.")
        ),
        required = listOf("description")
      )
      assertEquals(expectedParameters, funDec.parameters)
    }

    @Test
    fun getShowtimes() {
      val raw = """
        {
          "name": "get_showtimes",
          "description": "Find the start times for movies playing in a specific theater",
          "parameters": {
            "type": "OBJECT",
            "properties": {
              "location": {
                "type": "STRING",
                "description": "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"
              },
              "movie": {
                "type": "STRING",
                "description": "Any movie title"
              },
              "theater": {
                "type": "STRING",
                "description": "Name of the theater"
              },
              "date": {
                "type": "STRING",
                "description": "Date for requested showtime"
              }
            },
            "required": [
              "location",
              "movie",
              "theater",
              "date"
            ]
          }
        }
      """.trimIndent()
      val actual = Json.decodeFromString<FunctionDeclaration>(raw)
      val expected = FunctionDeclaration(
        name = "get_showtimes",
        description = "Find the start times for movies playing in a specific theater",
        Parameters(
          type = "OBJECT",
          properties = mapOf(
            "location" to Argument("STRING", "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"),
            "movie" to Argument("STRING", "Any movie title"),
            "theater" to Argument("STRING", "Name of the theater"),
            "date" to Argument("STRING", "Date for requested showtime"),
          ),
          required = listOf("location", "movie", "theater", "date")
        )
      )
      assertEquals(expected, actual)
    }
  }
}
