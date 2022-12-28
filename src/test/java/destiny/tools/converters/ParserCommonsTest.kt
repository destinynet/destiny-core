/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import destiny.core.Gender
import destiny.core.calendar.eightwords.Direction
import kotlin.test.Test
import kotlin.test.assertSame

class ParserCommonsTest {

  @Test
  fun parseEnum() {
    ParserCommons.run {

      assertSame(Gender.男, parseEnum("gender", Gender::class.java, mapOf("gender" to "男"), Gender.男))
      assertSame(Gender.男, parseEnum("gender", Gender::class.java, emptyMap(), Gender.男))
      assertSame(Gender.女, parseEnum("gender", Gender::class.java, emptyMap(), Gender.女))
      assertSame(Gender.女, parseEnum("gender", Gender::class.java, mapOf("gender" to "X"), Gender.女))

      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java, emptyMap(), Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java, mapOf("dir" to "L2R"), Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java, mapOf("dir" to "L2R"), Direction.R2L))

      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java, mapOf("dir" to "XXX"), Direction.R2L))
    }
  }
}
