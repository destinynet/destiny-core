/**
 * Created by smallufo on 2016-03-01.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsoToolTest {

  @Test
  fun testIsValidLanguage() {
    assertTrue(IsoTool.isValidCountry("TW"))
    assertTrue(IsoTool.isValidCountry("tw"))
    assertFalse(IsoTool.isValidCountry("TWN"))
  }

  @Test
  fun testIsValidCountry() {
    assertTrue(IsoTool.isValidLanguage("zh"))
    assertTrue(IsoTool.isValidLanguage("ZH"))
  }
}