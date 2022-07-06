/**
 * Created by smallufo on 2022-07-06.
 */
package destiny.tools.serializers

import destiny.core.iching.Hexagram
import kotlinx.serialization.json.Json
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
//
//  @Test
//  fun testSerializeWithModule() {
//    val format = Json {
//      serializersModule = SerializersModule {
//        contextual(IHexagramSerializer)
//      }
//    }
//    println(format.encodeToString(Hexagram.乾))
//  }

  @Test
  fun testException() {

    assertFailsWith(IllegalArgumentException::class) {
      Json.decodeFromString(IHexagramSerializer, """"11111x"""")
    }
  }
}
