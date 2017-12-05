package destiny.core.chinese.ziwei

import org.slf4j.LoggerFactory
import kotlin.test.Test

/**
 * Created by smallufo on 2017-12-03.
 */
class StrengthMiddleImplTest2 {

  private val logger = LoggerFactory.getLogger(javaClass)


  var strengthImpl : IStrength = StrengthMiddleImpl()

  @Test
  fun `list values`() {

    //logger.info("all values = {}" , StarMain.紫微.getAllValues())

    val a = StarMain.values
    logger.info("a = {}" , a)
    logger.info("{} : {}" , StarMain::class.simpleName , StarMain.values)
    logger.info("{} : {}" , StarLucky::class.simpleName , StarLucky.values)
    logger.info("{} : {}" , StarUnlucky::class.simpleName , StarUnlucky.values)
    logger.info("{} : {}" , StarMinor::class.simpleName , StarMinor.values)
    logger.info("{} : {}" , StarLongevity::class.simpleName , StarLongevity.values)
    logger.info("{} : {}" , StarDoctor::class.simpleName , StarDoctor.values)
    logger.info("{} : {}" , StarYearFront::class.simpleName , StarYearFront.values)
    logger.info("{} : {}" , StarGeneralFront::class.simpleName , StarGeneralFront.values)

//    for(star in StarMain.values) {
//      println(star)
//    }
//    println("list = ${StarMain.list}")
  }
}