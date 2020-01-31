/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.astrology

import mu.KotlinLogging
import java.util.*
import kotlin.test.*


class LunarNodeTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testToString() {
    assertEquals("北交點" , LunarNode.NORTH_MEAN.toString(Locale.TAIWAN))
    assertEquals("北交點" , LunarNode.NORTH_TRUE.toString(Locale.TAIWAN))

    assertEquals("北交点" , LunarNode.NORTH_MEAN.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("北交点" , LunarNode.NORTH_TRUE.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("North" , LunarNode.NORTH_MEAN.toString(Locale.ENGLISH))
    assertEquals("North" , LunarNode.NORTH_TRUE.toString(Locale.ENGLISH))

    for (each in LunarNode.meanArray) {
      assertNotNull(each.toString(Locale.TAIWAN))
      assertNotNull(each.toString(Locale.SIMPLIFIED_CHINESE))
      assertNotNull(each.toString(Locale.ENGLISH))
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
