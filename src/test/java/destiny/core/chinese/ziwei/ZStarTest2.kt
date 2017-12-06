package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
import kotlin.test.Test

/**
 * Created by smallufo on 2017-12-07.
 */
class ZStarTest2 {

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

    logger.info("map2 = {}", map2)
  }
}