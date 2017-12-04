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
  fun `StarMain values`() {
    logger.info("values = {}" , StarMain.values)
    //StarMain.list.forEach { println("$it") }
    for(star in StarMain.values) {
      println("$star")
    }
    println("list = ${StarMain.list}")
    logger.info("list = {}" , StarMain.list)
  }
}