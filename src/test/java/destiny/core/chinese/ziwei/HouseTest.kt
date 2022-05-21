/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.core.chinese.ziwei

import destiny.core.EnumTest
import destiny.tools.getTitle
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HouseTest : EnumTest() {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testString() {
    testEnums(House::class , false)
  }

  @Test
  fun testGetTitle() {

    assertEquals("命宮", House.命宮.getTitle(Locale.TAIWAN))
    assertEquals("遷移", House.遷移.getTitle(Locale.TAIWAN))
    assertEquals("迁移", House.遷移.getTitle(Locale.SIMPLIFIED_CHINESE))

    assertEquals("遷移", House.遷移.getTitle(Locale.ENGLISH))

    assertEquals("遷移", House.遷移.toString())

    House.values().forEach { house ->
      assertNotNull(house.toString())
      logger.info("{}", house.toString())
    }

  }

}
