/**
 * Created by smallufo on 2017-04-20.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory

import java.util.Locale

import destiny.core.chinese.Branch.子
import destiny.core.chinese.ziwei.StarMinor.三台
import destiny.core.chinese.ziwei.StarMinor.天巫
import destiny.core.chinese.ziwei.StarUnlucky.地劫
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class StrengthMiddleImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  internal var impl: IStrength = StrengthMiddleImpl()

  @Test
  fun testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }


  @Test
  fun testListStarByType() {

    val starList = listOf(
      *StarMain.values,
      *StarLucky.values,
      *StarUnlucky.values,
      *StarMinor.values,
      *StarDoctor.values,
      *StarLongevity.values
    )

    logger.info("starList = {}", starList)

    val map = starList.groupBy { it.type }
      .toSortedMap()

    map.forEach { (type, stars) ->
      logger.info("{}", type)
      stars.forEach { star -> logger.info("\t{} : {}", star, impl.getMapOf(star)) }
    }
  }


  @Test
  fun getMap() {
    impl.getMapOf(StarMain.紫微).forEach { (k, v) -> logger.info("{} -> {}", k, v) }

    impl.getMapOf(地劫).forEach { (k, v) -> logger.info("{} : {} -> {}", 地劫, k, v) }
  }

  @Test
  fun getStrength() {
    assertSame(5, impl.getStrengthOf(StarMain.紫微, 子))
    assertSame(5, impl.getStrengthOf(StarMain.破軍, Branch.亥))

    assertSame(2, impl.getStrengthOf(StarLucky.天魁, 子))
    assertSame(5, impl.getStrengthOf(三台, 子))
    assertNull(impl.getStrengthOf(天巫, 子))
  }
}