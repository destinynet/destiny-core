package destiny.tools

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EnumMapsTest {

  private enum class Impl { A, B, C }

  @Test
  fun `涵蓋全部 enum 值則通過`() {
    mapOf(Impl.A to 1, Impl.B to 2, Impl.C to 3).requireAllEnumKeys("full")
  }

  @Test
  fun `缺 key 時丟例外，訊息帶 map 名與缺的值`() {
    val e = assertFailsWith<IllegalArgumentException> {
      mapOf(Impl.A to 1).requireAllEnumKeys("partialMap")
    }
    val msg = e.message!!
    assertTrue("partialMap" in msg && "[B, C]" in msg, msg)
  }
}
