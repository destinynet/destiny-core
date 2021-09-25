/**
 * Created by smallufo on 2019-12-13.
 */
package destiny.core.chinese

import destiny.core.chinese.liuren.EnumTest
import destiny.tools.getDescription
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class YearTypeTest : EnumTest() {

  @Test
  fun testToString() {

    testEnums(YearType::class)

    assertEquals("YEAR_LUNAR" , YearType.YEAR_LUNAR.toString())
    assertEquals("YEAR_SOLAR" , YearType.YEAR_SOLAR.toString())

    assertEquals("初一為界" , YearType.YEAR_LUNAR.getTitle(Locale.TAIWAN))
    assertEquals("初一為界" , YearType.YEAR_LUNAR.getDescription(Locale.TAIWAN))
    assertEquals("立春為界" , YearType.YEAR_SOLAR.getTitle(Locale.TAIWAN))
    assertEquals("立春為界" , YearType.YEAR_SOLAR.getDescription(Locale.TAIWAN))

    assertEquals("初一为界" , YearType.YEAR_LUNAR.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("初一为界" , YearType.YEAR_LUNAR.getDescription(Locale.SIMPLIFIED_CHINESE))
    assertEquals("立春为界" , YearType.YEAR_SOLAR.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("立春为界" , YearType.YEAR_SOLAR.getDescription(Locale.SIMPLIFIED_CHINESE))

    assertEquals("初一為界" , YearType.YEAR_LUNAR.getTitle(Locale.ENGLISH))
    assertEquals("初一為界" , YearType.YEAR_LUNAR.getDescription(Locale.ENGLISH))
    assertEquals("初一為界" , YearType.YEAR_LUNAR.getTitle(Locale.FRANCE))
    assertEquals("初一為界" , YearType.YEAR_LUNAR.getDescription(Locale.FRANCE))
  }
}
