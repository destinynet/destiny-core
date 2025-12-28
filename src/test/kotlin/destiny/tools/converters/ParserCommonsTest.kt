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

      assertSame(Gender.M, parseEnum("gender", Gender::class.java, mapOf("gender" to "男"), Gender.M))
      assertSame(Gender.M, parseEnum("gender", Gender::class.java, emptyMap(), Gender.M))
      assertSame(Gender.F, parseEnum("gender", Gender::class.java, emptyMap(), Gender.F))
      assertSame(Gender.F, parseEnum("gender", Gender::class.java, mapOf("gender" to "X"), Gender.F))

      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java, emptyMap(), Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java, mapOf("dir" to "L2R"), Direction.R2L))

      assertSame(Direction.L2R, parseEnum("dir", Direction::class.java, mapOf("dir" to "L2R"), Direction.R2L))

      assertSame(Direction.R2L, parseEnum("dir", Direction::class.java, mapOf("dir" to "XXX"), Direction.R2L))
    }
  }
}
