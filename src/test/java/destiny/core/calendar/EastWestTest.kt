/**
 * Created by smallufo on 2020-05-27.
 */
package destiny.core.calendar

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class EastWestTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testString() {
    assertEquals("東經", EastWest.EAST.toString(Locale.TAIWAN))
    assertEquals("西經", EastWest.WEST.toString(Locale.TAIWAN))

    assertEquals("东经", EastWest.EAST.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("西经", EastWest.WEST.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("East", EastWest.EAST.toString(Locale.ENGLISH))
    assertEquals("West", EastWest.WEST.toString(Locale.ENGLISH))

    logger.info("EAST of {} = {}", Locale.FRANCE, EastWest.EAST.toString(Locale.FRANCE))
  }
}
