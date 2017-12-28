/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.astrology

import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class LunarApsisTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (each in LunarApsis.values) {
      assertNotNull(each.toString())
      logger.info("{}", each.toString())
    }

    val set =  LunarApsis.values.map { it.toString() }.toSet()
    assertTrue(set.contains("遠地點"))
    assertTrue(set.contains("近地點"))
  }

}