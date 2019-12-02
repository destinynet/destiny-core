/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.core.chinese.ziwei

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HouseTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testToString() {

    assertEquals("命宮" , House.命宮.toString(Locale.TAIWAN))
    assertEquals("遷移" , House.遷移.toString(Locale.TAIWAN))
    assertEquals("迁移" , House.遷移.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("遷移" , House.遷移.toString(Locale.ENGLISH))

    assertEquals("遷移" , House.遷移.toString())


    for (house in House.values()) {
      assertNotNull(house.toString())
      logger.info("{}", house.toString())
    }
  }

}
