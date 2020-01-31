/**
 * Created by smallufo on 2017-04-29.
 */
package destiny.core.chinese.impls

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class TianyiLiurenPithyImplTest {

  private val logger = KotlinLogging.logger { }

  private val impl = TianyiLiurenPithyImpl()

  @Test
  fun getTitle() {
    assertNotNull(impl.toString(Locale.TAIWAN))
    assertNotNull(impl.toString(Locale.SIMPLIFIED_CHINESE))
    assertNotNull(impl.getDescription(Locale.TAIWAN))
    assertNotNull(impl.getDescription(Locale.SIMPLIFIED_CHINESE))

    logger.info("tw = {} , cn = {}", impl.toString(Locale.TAIWAN), impl.toString(Locale.SIMPLIFIED_CHINESE))
    logger.info("tw = {} , cn = {}", impl.getDescription(Locale.TAIWAN), impl.getDescription(Locale.SIMPLIFIED_CHINESE))
  }

}
