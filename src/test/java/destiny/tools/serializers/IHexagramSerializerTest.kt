/**
 * Created by smallufo on 2022-07-06.
 */
package destiny.tools.serializers

import destiny.core.iching.Hexagram
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class IHexagramSerializerTest {

  @Test
  fun testSerialize() {
    Json.encodeToString(IHexagramSerializer, Hexagram.乾).also { raw ->
      assertEquals(""""111111"""", raw)
      assertEquals(Hexagram.乾, Json.decodeFromString(IHexagramSerializer, raw))
    }


  }

  @Test
  fun testException() {
    try {
      Json.decodeFromString(IHexagramSerializer, """"11111x"""")
      fail()
    } catch (e: Exception) {
      assertTrue { e is IllegalArgumentException }
    }

  }
}
