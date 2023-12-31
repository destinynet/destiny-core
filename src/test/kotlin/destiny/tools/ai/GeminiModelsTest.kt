/**
 * Created by smallufo on 2023-12-31.
 */
package destiny.tools.ai

import destiny.tools.ai.Gemini.FunctionDeclaration
import destiny.tools.ai.Gemini.FunctionDeclaration.Parameters
import destiny.tools.ai.Gemini.FunctionDeclaration.Parameters.Argument
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiTest {

  @Nested
  inner class FunctionDeclarationSerializationTests {

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
            "location" to Argument("STRING" , "The city and state, e.g. San Francisco, CA or a zip code e.g. 95616"),
            "movie" to Argument("STRING" , "Any movie title"),
            "theater" to Argument("STRING" , "Name of the theater"),
            "date" to Argument("STRING" , "Date for requested showtime"),
          ),
          required = listOf("location" , "movie" , "theater" , "date")
        )
      )
      assertEquals(expected, actual)
    }
  }
}
