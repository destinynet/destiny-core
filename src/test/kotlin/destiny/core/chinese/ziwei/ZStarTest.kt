package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

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

  /**
   * 把「星 → 地支」翻轉成「地支 → 星群」的兩種寫法，結果必須一致，且未落星的宮位為 null。
   *
   * 原本三個 map 都只是 log 出來對眼睛看（註解裡還寫著 "等同 map2 , perfect !"）——
   * 那個 "perfect" 從來沒有被驗證過。
   */
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

    assertEquals(
      mapOf(
        Branch.子 to listOf(StarMain.紫微, StarMain.天同),
        Branch.丑 to listOf(StarMain.七殺)
      ),
      map1
    )

    val map2 = Branch.entries.map { branch ->
      branch to map1[branch]
    }.toMap()

    val map3 = Branch.entries.map { branch ->
      branch to starBranchMap.entries.groupBy { it.value }
        .mapValues { it -> it.value.map { it.key } }[branch]
    }.toMap()

    // 兩種寫法等價
    assertEquals(map2, map3)

    // 12 宮都有 key，沒落星的為 null
    assertEquals(Branch.entries.toSet(), map2.keys)
    assertEquals(listOf(StarMain.紫微, StarMain.天同), map2[Branch.子])
    assertEquals(listOf(StarMain.七殺), map2[Branch.丑])
    assertNull(map2[Branch.寅])
    assertEquals(10, map2.count { it.value == null })
  }

  /** 依 [ZStar.type] 分組不得遺漏或重複；廟旺相關的驗證見 `StrengthMiddleImplTest` */
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

    // 用 multiset 比對：StarMinor 內有兩顆同名的旬空
    assertEquals(
      starList.groupingBy { it }.eachCount(),
      map1.values.flatten().groupingBy { it }.eachCount()
    )
    assertEquals(StarMain.values.toList(), map1.getValue(StarMain.紫微.type))
    assertTrue(map1.keys.size > 1, "應分出多種 type")
  }
}
