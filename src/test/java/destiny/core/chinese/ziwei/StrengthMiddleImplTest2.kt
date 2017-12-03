package destiny.core.chinese.ziwei

import org.slf4j.LoggerFactory
import kotlin.test.Test

/**
 * Created by smallufo on 2017-12-03.
 */
class StrengthMiddleImplTest2 {

  private val logger = LoggerFactory.getLogger(javaClass)

  private var impl: IStrength = StrengthMiddleImpl()

  @Test
  fun testListStarByType() {
    logger.info("StarMain.values = {}", StarMain.values)
  }
}