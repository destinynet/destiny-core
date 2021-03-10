/**
 * Created by smallufo on 2019-12-13.
 */
package destiny.core.chinese

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class YearTypeTest {

  @Test
  fun testToString() {
    assertEquals("YEAR_LUNAR" , YearType.YEAR_LUNAR.toString())
    assertEquals("YEAR_SOLAR" , YearType.YEAR_SOLAR.toString())

    assertEquals("初一為界" , YearType.YEAR_LUNAR.toString(Locale.TAIWAN))
    assertEquals("立春為界" , YearType.YEAR_SOLAR.toString(Locale.TAIWAN))

    assertEquals("初一为界" , YearType.YEAR_LUNAR.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("立春为界" , YearType.YEAR_SOLAR.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("初一為界" , YearType.YEAR_LUNAR.toString(Locale.ENGLISH))
    assertEquals("初一為界" , YearType.YEAR_LUNAR.toString(Locale.FRANCE))
  }
}
