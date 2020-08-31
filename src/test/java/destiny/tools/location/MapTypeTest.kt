/**
 * Created by smallufo on 2020-08-31.
 */
package destiny.tools.location

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class MapTypeTest {

  @Test
  fun testToString() {
    Locale.TAIWAN.also { locale ->
      assertEquals("道路" , MapType.roadmap.toString(locale))
      assertEquals("衛星" , MapType.satellite.toString(locale))
      assertEquals("混合" , MapType.hybrid.toString(locale))
      assertEquals("地形" , MapType.terrain.toString(locale))
    }

    Locale.SIMPLIFIED_CHINESE.also { locale ->
      assertEquals("道路" , MapType.roadmap.toString(locale))
      assertEquals("卫星" , MapType.satellite.toString(locale))
      assertEquals("混合" , MapType.hybrid.toString(locale))
      assertEquals("地形" , MapType.terrain.toString(locale))
    }

  }
}
