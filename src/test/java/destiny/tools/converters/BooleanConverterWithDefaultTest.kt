/**
 * Created by smallufo on 2022-03-12.
 */
package destiny.tools.converters

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class BooleanConverterWithDefaultTest {

  @Test
  fun testConvert() {
    val key = "key"
    val converter = object : BooleanConverterWithDefault(key, false) {}
    assertNull(converter.getContext(emptyMap()))
    assertFalse(converter.getContextWithDefault(emptyMap()))
    assertFalse(converter.getContextWithDefault(mapOf(key to "false")))
    assertFalse(converter.getContextWithDefault(mapOf(key to "FALSE")))
    assertTrue(converter.getContextWithDefault(mapOf(key to "true")))
    assertTrue(converter.getContextWithDefault(mapOf(key to "TRUE")))
  }
}
