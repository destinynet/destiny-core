package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

/**
 * Created by smallufo on 2017-12-07.
 */
class ZStarTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testSerialize() {
    ZStar.values.forEach { s ->
      val rawJson = Json.encodeToString(s)
      logger.info { "$s = $rawJson" }
      assertSame(s, decodeFromString(rawJson))
    }
  }


  @Test
  fun shouldNotEqual() {
    logger.info("StarDoctor.小耗 = {}" , StarDoctor.小耗)
    logger.info("StarYearFront.小耗 = {}" , StarYearFront.小耗)
    assertNotEquals<Any>(StarDoctor.小耗 , StarYearFront.小耗)
    assertNotEquals<ZStar>(StarDoctor.小耗 , StarYearFront.小耗)
    assertNotEquals<ZStar>(StarDoctor.博士 , StarDoctor.力士)
    assertNotEquals<StarDoctor>(StarDoctor.博士 , StarDoctor.力士)
  }

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


    val map2 = Branch.entries.map { branch ->
      branch to map1[branch]
    }.toMap()

    // {子=[紫微, 天同], 丑=[七殺], 寅=null, 卯=null, 辰=null, 巳=null, 午=null, 未=null, 申=null, 酉=null, 戌=null, 亥=null}
    logger.info("map2 = {}", map2)


    val map3 = Branch.entries.map { branch ->
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
