/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.astrology

import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue


class LunarNodeTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (each in LunarNode.values) {
      assertNotNull(each.toString())
      logger.info("{}", each.toString())
    }


    val set = LunarNode.values.map { it.toString() }.toSet()
    assertTrue(set.contains("北交點"))
    assertTrue(set.contains("南交點"))
  }

  @Test
  fun testEquals() {
    assertSame(LunarNode.NORTH_MEAN, LunarNode.of(LunarNode.NorthSouth.NORTH, NodeType.MEAN))
    assertSame(LunarNode.NORTH_TRUE, LunarNode.of(LunarNode.NorthSouth.NORTH, NodeType.TRUE))

    assertSame(LunarNode.SOUTH_MEAN, LunarNode.of(LunarNode.NorthSouth.SOUTH, NodeType.MEAN))
    assertSame(LunarNode.SOUTH_TRUE, LunarNode.of(LunarNode.NorthSouth.SOUTH, NodeType.TRUE))
  }
}