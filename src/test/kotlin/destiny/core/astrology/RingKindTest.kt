/**
 * Created by smallufo on 2026-11-16.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.EventSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RingKindTest {

  @Test
  fun `行運、次限、太陽弧對得上同名的推運來源，其餘沒有`() {
    assertEquals(EventSource.TRANSIT, RingKind.TRANSIT.source)
    assertEquals(EventSource.SECONDARY, RingKind.SECONDARY.source)
    assertEquals(EventSource.SOLAR_ARC, RingKind.SOLAR_ARC.source)
    (RingKind.entries - setOf(RingKind.TRANSIT, RingKind.SECONDARY, RingKind.SOLAR_ARC)).forEach { assertNull(it.source, it.name) }
  }

  @Test
  fun `星座帶沒有推運來源`() {
    assertTrue(RingKind.entries.filter { it.band }.all { it.source == null })
  }
}
