/**
 * Created by smallufo on 2022-03-12.
 */
package destiny.tools.converters

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DoubleConverterWithDefaultTest {

  @Test
  fun testConvert() {
    val key = "key"
    val converter = object : DoubleConverterWithDefault(key, 0.0) {}

    assertNull(converter.getContext(emptyMap()))
    assertEquals(0.0, converter.getContextWithDefault(emptyMap()))
    assertEquals(0.1, converter.getContextWithDefault(mapOf(key to "0.1")))
    assertEquals(0.9, converter.getContextWithDefault(mapOf(key to "0.90")))
  }
}
