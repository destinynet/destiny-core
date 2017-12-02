/**
 * Created by smallufo on 2017-12-02.
 */
package destiny.core.chinese.ziwei

import kotlin.test.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertNotNull

class HouseTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (house in House.values()) {
      assertNotNull(house.toString())
      logger.info("{}", house.toString())
    }
  }

}