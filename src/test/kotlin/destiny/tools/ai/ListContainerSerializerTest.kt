/**
 * Created by smallufo on 2025-08-13.
 */
package destiny.tools.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ListContainerSerializerTest {

  private val json = Json {
    prettyPrint = true
    isLenient = true
  }

  @Serializable
  data class SimpleUser(val name: String, val age: Int)

  @Test
  fun `反序列化 - 標準物件格式的整數列表`() {
    val jsonString = """{ "array": [1, 2, 3] }"""
    val serializer = listContainerSerializer<Int>()
    val result = json.decodeFromString(serializer, jsonString)
    assertEquals(ListContainer(listOf(1, 2, 3)), result)
  }

  @Test
  fun `反序列化 - 直接陣列格式的整數列表`() {
    val jsonString = """{ "array": [1, 2, 3] }"""
    val serializer = listContainerSerializer<Int>()  // 更簡潔的語法
    val result = json.decodeFromString(serializer, jsonString)
    assertEquals(ListContainer(listOf(1, 2, 3)), result)
  }

  // --- 字串列表 (List<String>) 測試 ---

  @Test
  fun `反序列化 - 標準物件格式的字串列表`() {
    val jsonString = """{ "array": ["aa", "bb", "cc"] }"""
    val serializer = listContainerSerializer<String>()
    val result = json.decodeFromString(serializer, jsonString)
    assertEquals(ListContainer(listOf("aa", "bb", "cc")), result)
  }

  @Test
  fun `反序列化 - 直接陣列格式的字串列表`() {
    val jsonString = """["aa", "bb", "cc"]"""
    val serializer = listContainerSerializer<String>()
    val result = json.decodeFromString(serializer, jsonString)
    assertEquals(ListContainer(listOf("aa", "bb", "cc")), result)
  }

  // --- 自訂物件列表 (List<SimpleUser>) 測試 ---

  @Test
  fun `反序列化 - 標準物件格式的自訂物件列表`() {
    val jsonString = """
      { 
        "array": [
          { "name": "Kevin", "age": 30 },
          { "name": "Ivy", "age": 28 }
        ] 
      }
    """.trimIndent()
    val serializer = listContainerSerializer<SimpleUser>()
    val result = json.decodeFromString(serializer, jsonString)
    val expected = ListContainer(listOf(SimpleUser("Kevin", 30), SimpleUser("Ivy", 28)))
    assertEquals(expected, result)
  }

  @Test
  fun `反序列化 - 直接陣列格式的自訂物件列表`() {
    val jsonString = """
      [
        { "name": "Kevin", "age": 30 },
        { "name": "Ivy", "age": 28 }
      ]
    """.trimIndent()
    val serializer = listContainerSerializer<SimpleUser>()
    val result = json.decodeFromString(serializer, jsonString)
    val expected = ListContainer(listOf(SimpleUser("Kevin", 30), SimpleUser("Ivy", 28)))
    assertEquals(expected, result)
  }

  // --- 序列化測試 ---

  @Test
  fun `序列化 - 確保總是產生標準物件格式`() {
    val userList = ListContainer(listOf(SimpleUser("Bob", 40)))
    val serializer = ListContainerSerializer(SimpleUser.serializer())
    val resultString = json.encodeToString(serializer, userList)

    // 驗證輸出的字串是標準格式，且內容正確
    // 這裡我們將其解析回來，驗證其結構
    val jsonObject = json.parseToJsonElement(resultString)
    assert(jsonObject is kotlinx.serialization.json.JsonObject)
    assert((jsonObject as kotlinx.serialization.json.JsonObject).containsKey("array"))
    assertEquals(
      """{"array":[{"name":"Bob","age":40}]}""",
      resultString.replace("\\s".toRegex(), "") // 移除所有空白字元以進行比較
    )
  }

}
