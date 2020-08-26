/**
 * Created by smallufo on 2020-08-26.
 */
package destiny.core.chinese.ziwei

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull


class ViewSettingsTest {

  @Test
  fun testSelfTransFour() {
    ViewSettings.SelfTransFour.values().forEach { value ->
      assertNotNull(value.toString())
      assertNotNull(value.toString(Locale.TAIWAN))
      assertNotNull(value.toString(Locale.SIMPLIFIED_CHINESE))
      assertNotNull(value.toString(Locale.ENGLISH))
      assertNotNull(value.toString(Locale.FRANCE))
    }
  }

  @Test
  fun testOppoTransFour() {
    ViewSettings.OppoTransFour.values().forEach { value ->
      assertNotNull(value.toString())
      assertNotNull(value.toString(Locale.TAIWAN))
      assertNotNull(value.toString(Locale.SIMPLIFIED_CHINESE))
      assertNotNull(value.toString(Locale.ENGLISH))
      assertNotNull(value.toString(Locale.FRANCE))
    }
  }
}
