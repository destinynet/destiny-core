/**
 * Created by smallufo on 2026-04-20.
 */
package destiny.tools.ai

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class MsgCacheTest {

  @Test
  fun `cacheTtl defaults to null`() {
    val m = Msg(Role.SYSTEM, "hello", cacheable = true)
    assertNull(m.cacheTtl)
  }

  @Test
  fun `cacheTtl can be set via 4-arg constructor`() {
    val m = Msg(Role.SYSTEM, "hello", cacheable = true, cacheTtl = CachePromptTtl.LONG)
    assertEquals(CachePromptTtl.LONG, m.cacheTtl)
  }

  @Test
  fun `cacheTtl is independent of cacheable flag`() {
    // non-cacheable + ttl set is allowed (provider ignores ttl when cacheable=false)
    val m = Msg(Role.SYSTEM, "x", cacheable = false, cacheTtl = CachePromptTtl.SHORT)
    assertEquals(CachePromptTtl.SHORT, m.cacheTtl)
  }
}
