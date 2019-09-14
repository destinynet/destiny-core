/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.astrology

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue


class LunarNodeTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (each in LunarNode.meanArray) {
      assertNotNull(each)
      assertNotNull(each.toString())
      logger.info("{}", each.toString())
    }


    val set = LunarNode.meanArray.map { it.toString(Locale.TAIWAN) }.toSet()
    assertTrue(set.contains("北交點"))
    assertTrue(set.contains("南交點"))
  }

  @Test
  fun testEquals() {
    assertSame(LunarNode.NORTH_MEAN, LunarNode.of(NorthSouth.NORTH, NodeType.MEAN))
    assertSame(LunarNode.NORTH_TRUE, LunarNode.of(NorthSouth.NORTH, NodeType.TRUE))

    assertSame(LunarNode.SOUTH_MEAN, LunarNode.of(NorthSouth.SOUTH, NodeType.MEAN))
    assertSame(LunarNode.SOUTH_TRUE, LunarNode.of(NorthSouth.SOUTH, NodeType.TRUE))
  }
}