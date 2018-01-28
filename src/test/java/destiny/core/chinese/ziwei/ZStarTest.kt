package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
import kotlin.test.Test

/**
 * Created by smallufo on 2017-12-07.
 */
class ZStarTest {

  private val logger = LoggerFactory.getLogger(javaClass)


  @Test
  fun testMapGroupBy() {
    // 嘗試先建立一個簡單的 starBranchMap
    val starBranchMap = mapOf(
      StarMain.七殺 to Branch.丑,
      StarMain.紫微 to Branch.子,
      StarMain.天同 to Branch.子
    )
    // 哪個地支 裡面 有哪些星體
    val map1 = starBranchMap.entries
      .groupBy { it.value }
      .mapValues { it -> it.value.map { it.key } }
      .toSortedMap()

    logger.info("map1 = {}", map1)  // map1 = {子=[紫微, 天同], 丑=[七殺]}


    val map2 = Branch.values().map { branch ->
      branch to map1[branch]
    }.toMap()

    // {子=[紫微, 天同], 丑=[七殺], 寅=null, 卯=null, 辰=null, 巳=null, 午=null, 未=null, 申=null, 酉=null, 戌=null, 亥=null}
    logger.info("map2 = {}", map2)


    val map3 = Branch.values().map { branch ->
      branch to starBranchMap.entries.groupBy { it.value }
        .mapValues { it -> it.value.map { it.key } }[branch]
    }.toMap()

    // 等同 map2 , perfect !
    logger.info("map3 = {}" , map3)
  }

  @Test
  fun testListStarByType() {
    val starList = listOf(
      *StarMain.values ,
      *StarLucky.values ,
      *StarUnlucky.values ,
      *StarMinor.values ,
      *StarDoctor.values ,
      *StarLongevity.values
    )

    val map1 = starList
      .groupBy { it.type }
      .toSortedMap()

    map1.forEach { (type, stars) ->
      logger.info("{}" , type)
      for(star in stars) {
        logger.info("\t{}" , star)
      }
    }
  }
}