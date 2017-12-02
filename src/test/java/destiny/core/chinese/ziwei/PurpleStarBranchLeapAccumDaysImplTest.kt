/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Locale

import destiny.core.chinese.Branch.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class PurpleStarBranchLeapAccumDaysImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  internal var impl = PurpleStarBranchLeapAccumDaysImpl()

  @Test
  fun testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  @Test
  @Throws(Exception::class)
  fun water2() {
    assertEquals(巳, impl.water2(32))
    assertEquals(巳, impl.water2(33))

    assertEquals(申, impl.water2(38))
    assertEquals(申, impl.water2(39))

    assertEquals(丑, impl.water2(48))
    assertEquals(丑, impl.water2(49))

    assertEquals(午, impl.water2(58))
    assertEquals(午, impl.water2(59))

    assertEquals(未, impl.water2(60))
  }

}