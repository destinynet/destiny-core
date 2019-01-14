/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import destiny.core.calendar.eightwords.Direction
import kotlin.test.Test
import kotlin.test.assertSame

class ParserCommonsTest {

  @Test
  fun parseEnum() {
    ParserCommons.run {
      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java, emptyMap(), Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java,
                                          mutableMapOf<String, String>().apply { put("dir", "L2R") }, Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java,
                                          mutableMapOf<String, String>().apply { put("dir", "L2R") }, Direction.R2L))

      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java,
                                          mutableMapOf<String, String>().apply { put("dir", "XXX") }, Direction.R2L))
    }
  }
}