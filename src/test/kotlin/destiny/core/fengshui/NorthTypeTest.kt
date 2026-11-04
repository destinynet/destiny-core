/**
 * Created by smallufo on 2020-08-31.
 */
package destiny.core.fengshui

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NorthTypeTest {

  @Test
  fun testToString() {
    Locale.TAIWAN.also { locale ->
      assertEquals("正北", NorthType.TRUE.getTitle(locale))
      assertEquals("磁北", NorthType.MAGNETIC.getTitle(locale))
    }

    Locale.SIMPLIFIED_CHINESE.also { locale ->
      assertEquals("正北", NorthType.TRUE.getTitle(locale))
      assertEquals("磁北", NorthType.MAGNETIC.getTitle(locale))
    }
  }
}
