/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.酉
import org.slf4j.LoggerFactory
import kotlin.test.Test

class ISmallRangeTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun getRanges() {
    logger.info("{}", ISmallRange.getRanges(Branch.申, 酉, Gender.男))
  }

}