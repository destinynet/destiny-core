/**
 * Created by smallufo on 2024-07-05.
 */
package destiny.core.astrology

import destiny.core.Gender
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class DiceModelTest {

  val logger = KotlinLogging.logger {}

  @Test
  fun testSerialization() {
    val model = DiceModel(DiceStar.JUPITER, ZodiacSign.TAURUS, 2, Gender.女, "詢問事業")
    Json.encodeToString(model).also { raw ->
      val actual = Json.decodeFromString<JsonElement>(raw)
      val expected = Json.decodeFromString<JsonElement>(
        """
        {
          "star": "JUPITER",
          "sign": "TAURUS",
          "house": 2,
          "gender": "女",
          "question": "詢問事業"
        }
      """.trimIndent()
      )
      assertEquals(expected, actual)
      val decoded = Json.decodeFromString<DiceModel>(raw)
      assertEquals(model, decoded)
    }
  }
}
