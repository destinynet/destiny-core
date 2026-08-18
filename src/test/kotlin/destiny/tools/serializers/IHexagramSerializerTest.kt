/**
 * Created by smallufo on 2022-07-06.
 */
package destiny.tools.serializers

import destiny.core.iching.Hexagram
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IHexagramSerializerTest {

  @Test
  fun testSerialize() {
    Json.encodeToString(IHexagramSerializer, Hexagram.乾).also { raw ->
      assertEquals(""""111111"""", raw)
      assertEquals(Hexagram.乾, Json.decodeFromString(IHexagramSerializer, raw))
    }
  }

  /**
   * 把 [IHexagramSerializer] 註冊成 `contextual` **不會**改變 [Hexagram] 的預設輸出 ——
   * contextual 只在欄位標了 `@Contextual`（或明確指定 serializer）時才會被查詢，
   * 而 [Hexagram] 本身是 enum，`encodeToString` 走的是它自己的 enum serializer。
   *
   * 原本這段是註解掉的 `println(...)`，看不出結論；這裡改成斷言，把「註冊了也沒用」寫死。
   * 要拿到 `"111111"` 就得像 [testSerialize] 那樣顯式傳入 serializer。
   */
  @Test
  fun `註冊成 contextual 不會影響 enum 的預設輸出`() {
    val format = Json {
      serializersModule = SerializersModule {
        contextual(IHexagramSerializer)
      }
    }

    assertEquals(""""乾"""", format.encodeToString(Hexagram.乾))
    assertEquals(Json.encodeToString(Hexagram.乾), format.encodeToString(Hexagram.乾))
  }

  @Test
  fun testException() {

    assertFailsWith(IllegalArgumentException::class) {
      Json.decodeFromString(IHexagramSerializer, """"11111x"""")
    }
  }
}
