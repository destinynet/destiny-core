package destiny.tools.ai.llm

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by smallufo on 2024-10-14.
 */
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
          "model": "claude-3-TEST-MODEL",
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
            "output_tokens": 92,
            "cache_creation_input_tokens":0,
            "cache_read_input_tokens":0
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
        "model": "claude-3-TEST-MODEL",
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
          "output_tokens": 271,
          "cache_creation_input_tokens":0,
          "cache_read_input_tokens":0
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


  /**
   * 2026-04-18：驗證 prompt caching 的 request payload 序列化：
   * - 頂層 `system` 陣列 + `cache_control: ephemeral`
   * - `Content.Text` 也支援 `cache_control`（給 cacheable user message 用）
   */
  @Nested
  inner class PromptCachingSerialization {

    @Test
    fun `system with cache_control ephemeral 序列化正確`() {
      val chatModel = Claude.ChatModel(
        messages = listOf(
          Claude.ClaudeMessage.TextContent("user", "今天運勢如何？"),
        ),
        model = "claude-sonnet-4-5",
        maxTokens = 1024,
        system = listOf(
          Claude.SystemTextBlock(
            text = "[USER_PROFILE]\n出生資料：男性，1970-01-01\n\n[NATAL_DATA]\n甲子年 壬申月 乙亥日 戊寅時",
            cacheControl = Claude.CacheControl(),
          )
        ),
      )

      val serialized = json.encodeToString(Claude.ChatModel.serializer(), chatModel)
      logger.info { "serialized: $serialized" }

      val doc = JsonPath.parse(serialized)
      assertEquals("text", doc.read("$.system[0].type"))
      assertEquals("ephemeral", doc.read("$.system[0].cache_control.type"))
      val systemText: String = doc.read("$.system[0].text")
      assertTrue(systemText.contains("[USER_PROFILE]"))
      assertTrue(systemText.contains("[NATAL_DATA]"))
      assertTrue(systemText.contains("1970-01-01"))
    }

    @Test
    fun `system 為 null 時 不出現在 payload`() {
      val chatModel = Claude.ChatModel(
        messages = listOf(Claude.ClaudeMessage.TextContent("user", "hi")),
        model = "claude-sonnet-4-5",
        system = null,
      )

      val serialized = json.encodeToString(Claude.ChatModel.serializer(), chatModel)
      val doc = JsonPath.parse(serialized)
      assertFailsWith<PathNotFoundException>("system=null 時不該出現") {
        doc.read<Any>("$.system")
      }
    }

    @Test
    fun `Text content 的 cache_control 可選序列化`() {
      val textWithCache = Claude.Content.Text(text = "cached content", cacheControl = Claude.CacheControl())
      val textWithoutCache = Claude.Content.Text(text = "normal content")

      val s1 = json.encodeToString(Claude.Content.serializer(), textWithCache)
      val s2 = json.encodeToString(Claude.Content.serializer(), textWithoutCache)

      val d1 = JsonPath.parse(s1)
      assertEquals("ephemeral", d1.read<String>("$.cache_control.type"))
      assertEquals("cached content", d1.read<String>("$.text"))

      val d2 = JsonPath.parse(s2)
      assertEquals("normal content", d2.read<String>("$.text"))
      assertFailsWith<PathNotFoundException>("無 cacheControl 時 explicitNulls=false 應省略") {
        d2.read<Any>("$.cache_control")
      }
    }

    @Test
    fun `response usage 含 cache 欄位反序列化`() {
      val raw = """
        {
          "id": "msg_test",
          "type": "message",
          "role": "assistant",
          "model": "claude-sonnet-4-5",
          "content": [{"type": "text", "text": "ok"}],
          "stop_reason": "end_turn",
          "stop_sequence": null,
          "usage": {
            "input_tokens": 100,
            "output_tokens": 50,
            "cache_creation_input_tokens": 1500,
            "cache_read_input_tokens": 3200
          }
        }
      """.trimIndent()

      val response = json.decodeFromString<Claude.Response>(raw)
      val usage = assertNotNull(response.usage)
      assertEquals(100, usage.inputTokens)
      assertEquals(50, usage.outputTokens)
      assertEquals(1500, usage.cacheCreationInputTokens)
      assertEquals(3200, usage.cacheReadInputTokens)
    }
  }
}
