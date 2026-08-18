/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.酉
import kotlin.test.Test
import kotlin.test.assertEquals

class ISmallRangeTest {

  /**
   * 小限：每 12 年循環一次，故傳回的八個歲數必為等差 12。
   * 男女起點不同（此例男從 2 歲起、女從 12 歲起），原本只有 logger.info，起點算錯也不會紅。
   */
  @Test
  fun getRanges() {
    assertEquals(listOf(2, 14, 26, 38, 50, 62, 74, 86), ISmallRange.getRanges(Branch.申, 酉, Gender.M))
    assertEquals(listOf(12, 24, 36, 48, 60, 72, 84, 96), ISmallRange.getRanges(Branch.申, 酉, Gender.F))

    listOf(Gender.M, Gender.F).forEach { gender ->
      val ranges = ISmallRange.getRanges(Branch.申, 酉, gender)
      assertEquals(8, ranges.size)
      assertEquals(List(7) { 12 }, ranges.zipWithNext { a, b -> b - a })
    }
  }

}
