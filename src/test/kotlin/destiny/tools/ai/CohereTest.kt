/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CohereTest {

  @OptIn(ExperimentalSerializationApi::class)
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
    fun demo() {
      val raw = """
        {
           "model":"command-r-plus-08-2024",
           "tools":[
              {
                 "type":"function",
                 "function":{
                    "name":"query_daily_sales_report",
                    "description":"Connects to a database to retrieve overall sales volumes and sales information for a given day.",
                    "parameters":{
                       "day":{
                          "description":"Retrieves sales data for this day, formatted as YYYY-MM-DD.",
                          "type":"str",
                          "required":true
                       }
                    }
                 }
              },
              {
                 "type":"function",
                 "function":{
                    "name":"query_product_catalog",
                    "description":"Connects to a a product catalog with information about all the products being sold, including categories, prices, and stock levels.",
                    "parameters":{
                       "category":{
                          "description":"Retrieves product information data for all products in this category.",
                          "type":"str",
                          "required":true
                       }
                    }
                 }
              }
           ],
           "messages":[
           ]
        }
      """.trimIndent()
      json.decodeFromString<Cohere.Request>(raw).also { req ->
        logger.info { req }
        assertEquals(2, req.tools.size)
        req.tools[0].function.also { f ->
          assertEquals("query_daily_sales_report", f.name)
          assertEquals(1, f.parameters.size)
          f.parameters["day"].also { para ->
            assertNotNull(para)
            assertEquals("str", para.type)
            assertTrue { para.required }
          }

        }
        req.tools[1].function.also { f ->
          assertEquals("query_product_catalog", f.name)
          assertEquals(1, f.parameters.size)
          f.parameters["category"].also { para ->
            assertNotNull(para)
            assertEquals("str", para.type)
            assertTrue { para.required }
          }

        }
      }
    }
  }
}
