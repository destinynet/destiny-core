/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionsKtTest {

  @Test
  fun firstNotNullResult_iterable() {
    listOf(null, 'A', 'B').also { list ->
      assertEquals('A', list.firstNotNullResult { it })
      assertEquals('a', list.firstNotNullResult { it?.lowercaseChar() })
    }

    listOf<Char?>(null, null).also { list ->
      assertNull(list.firstNotNullResult { it })
      assertNull(list.firstNotNullResult { it?.lowercaseChar() })
    }
  }

  @Test
  fun firstNotNullResult_sequence() {
    sequenceOf(null, 'A', 'B').also { seq ->
      assertEquals('A', seq.firstNotNullResult { it })
      assertEquals('a', seq.firstNotNullResult { it?.lowercaseChar() })
    }

    sequenceOf<String?>(null, null).also { seq ->
      assertNull(seq.firstNotNullResult { it })
      assertNull(seq.map { it }.firstNotNullResult { it?.lowercase(Locale.getDefault()) })
    }
  }

  // ── JsonElement.toMap / toAny ────────────────────────────────────────────
  // 迴歸守衛：primitive 的解碼一旦被內聯回 toMap() 的 JsonObject 分支，
  // 陣列元素就會回頭呼叫 toMap() 並撞上「非物件即 emptyMap」，`["A","B"]` 變成 `[{}, {}]`。

  private fun parse(s: String): JsonElement = Json.decodeFromString<JsonElement>(s)

  @Test
  fun toMap_scalars() {
    val m = parse("""{"s":"abc","b":true,"i":42,"d":1.5}""").toMap()
    assertEquals("abc", m["s"])
    assertEquals(true, m["b"])
    assertEquals(42, m["i"])
    assertEquals(1.5, m["d"])
  }

  @Test
  fun toMap_stringArray_keepsValues() {
    val m = parse("""{"aspects":["CONJUNCTION","SQUARE"]}""").toMap()
    assertEquals(listOf("CONJUNCTION", "SQUARE"), m["aspects"])
  }

  @Test
  fun toMap_numberArray_keepsValues() {
    assertEquals(listOf(1, 2, 3), parse("""{"a":[1,2,3]}""").toMap()["a"])
  }

  @Test
  fun toMap_objectArray_unchangedFromLegacyBehaviour() {
    // 舊行為就是 List<Map<String,Any>> —— 這一格不可改變
    assertEquals(
      listOf(mapOf("k" to "v"), mapOf("k" to "w")),
      parse("""{"a":[{"k":"v"},{"k":"w"}]}""").toMap()["a"]
    )
  }

  @Test
  fun toMap_nestedObject() {
    assertEquals(mapOf("y" to 1), parse("""{"x":{"y":1}}""").toMap()["x"])
  }

  @Test
  fun toMap_nullValuedKeyIsDropped() {
    val m = parse("""{"a":null,"b":1}""").toMap()
    assertTrue("a" !in m)
    assertEquals(1, m["b"])
  }

  @Test
  fun toMap_nonObjectRootIsEmpty() {
    assertEquals(emptyMap(), parse("""["a","b"]""").toMap())
    assertEquals(emptyMap(), parse(""""bare"""").toMap())
  }

  @Test
  fun toAny_jsonNullIsNull() {
    assertNull(parse("null").toAny())
  }
}
