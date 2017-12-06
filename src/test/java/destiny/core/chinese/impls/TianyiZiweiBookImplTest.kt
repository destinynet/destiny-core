/**
 * Created by smallufo on 2017-04-29.
 */
package destiny.core.chinese.impls

import kotlin.test.Test
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertNotNull

class TianyiZiweiBookImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  private val impl = TianyiZiweiBookImpl()

  @Test
  fun getTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertNotNull(impl.getDescription(Locale.TAIWAN))
    assertNotNull(impl.getDescription(Locale.SIMPLIFIED_CHINESE))

    logger.info("tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("tw = {} , cn = {}", impl.getDescription(Locale.TAIWAN), impl.getDescription(Locale.SIMPLIFIED_CHINESE))
  }

}