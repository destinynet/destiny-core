/**
 * Created by smallufo on 2019-12-08.
 */
package destiny.core.calendar

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class NorthSouthTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testString() {
    assertEquals("北緯" , NorthSouth.NORTH.toString(Locale.TAIWAN))
    assertEquals("南緯" , NorthSouth.SOUTH.toString(Locale.TAIWAN))

    assertEquals("北纬" , NorthSouth.NORTH.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("南纬" , NorthSouth.SOUTH.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("North" , NorthSouth.NORTH.toString(Locale.ENGLISH))
    assertEquals("South" , NorthSouth.SOUTH.toString(Locale.ENGLISH))

    logger.info("NORTH of {} = {}" , Locale.FRANCE , NorthSouth.NORTH.toString(Locale.FRANCE))

  }
}
