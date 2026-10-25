package destiny.core.calendar.chinese

import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertEquals

class ChineseDateWestYearTest {
  @Test
  fun westYearOf_matchesInstanceProperty() {
    assertEquals(1984, ChineseDate.westYearOf(78, StemBranch.甲子))
    assertEquals(1893, ChineseDate.westYearOf(76, StemBranch.癸巳))
    assertEquals(ChineseDate(78, StemBranch.甲子, 1, false, 1).westYear, ChineseDate.westYearOf(78, StemBranch.甲子))
  }
}
