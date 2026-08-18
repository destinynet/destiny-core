/**
 * Created by smallufo on 2017-04-20.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ziwei.StarMinor.三台
import destiny.core.chinese.ziwei.StarMinor.天巫
import destiny.core.chinese.ziwei.StarUnlucky.地劫
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StrengthMiddleImplTest {

  private val logger = KotlinLogging.logger { }

  internal var impl: IStrength = StrengthMiddleImpl()

  @Test
  fun testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }


  /**
   * 依 [ZStar.type] 分組不得遺漏或重複；且任何星的廟旺值都必須落在 1..7。
   *
   * 原本這裡是把 90 幾顆星、每顆 12 地支的廟旺表整份 log 出來 ——
   * 資料錯了只會印出錯的東西，不會紅。這裡改成驗**不變量**：
   * 逐格釘死近千個數字既難維護、也擋不住新增星曜，驗結構與值域才擋得住實際會發生的迴歸。
   */
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

    val map = starList.groupBy { it.type }.toSortedMap()

    // 分組不遺漏、不重複（用 multiset 比對，因為 StarMinor 內有同名的旬空兩顆）
    assertEquals(
      starList.groupingBy { it }.eachCount(),
      map.values.flatten().groupingBy { it }.eachCount()
    )

    // 14 主星自成一類，且每顆都有完整 12 地支的廟旺
    assertEquals(14, StarMain.values.size)
    assertEquals(StarMain.values.toList(), map.getValue(StarMain.紫微.type))
    StarMain.values.forEach { star ->
      assertEquals(12, impl.getMapOf(star).size, "主星 $star 應有 12 地支的廟旺")
    }

    // 廟旺值域
    starList.forEach { star ->
      impl.getMapOf(star).forEach { (branch, strength) ->
        assertTrue(strength in 1..7, "$star 在 $branch 的廟旺 $strength 應落在 1..7")
      }
    }
  }


  /** 原本只把兩張表 log 出來 */
  @Test
  fun getMap() {
    assertEquals(
      mapOf(
        子 to 5, 丑 to 1, 寅 to 1, 卯 to 2, 辰 to 7, 巳 to 2,
        午 to 1, 未 to 1, 申 to 2, 酉 to 5, 戌 to 6, 亥 to 2
      ),
      impl.getMapOf(StarMain.紫微)
    )
    // 與逐點查詢的 API 必須一致
    assertEquals(impl.getMapOf(StarMain.紫微)[子], impl.getStrengthOf(StarMain.紫微, 子))

    assertEquals(12, impl.getMapOf(地劫).size)

    // 並非每顆星都有完整 12 地支：天巫只在四馬地有廟旺
    assertEquals(mapOf(寅 to 5, 巳 to 7, 申 to 7, 亥 to 5), impl.getMapOf(天巫))
    assertNull(impl.getStrengthOf(天巫, 子), "天巫在子無廟旺")
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
