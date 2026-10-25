/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core

import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChartIdentityDtoTest {

  @Test
  fun formatsSolarAndLunarText() {
    val id = ChartIdentityDto.of("王小明", Gender.F, 1984, 9, 10, 8, 5, ChineseDate(78, StemBranch.甲子, 8, true, 15), "台北市")
    assertEquals(ChartIdentityDto("王小明", "F", "1984-09-10 08:05", "甲子年閏八月十五", "台北市"), id)
  }

  /** 只知道日期或時辰：國曆只印到日 */
  @Test
  fun dateOnlyWhenNoClockTime() {
    assertEquals("1984-09-10", ChartIdentityDto.of(null, null, 1984, 9, 10, 8, null, null, null).birthText)
    assertEquals("1984-09-10", ChartIdentityDto.of(null, null, 1984, 9, 10, null, null, null, null).birthText)
  }

  @Test
  fun blankNameAndPlaceDropped() {
    val id = ChartIdentityDto.of(" ", Gender.M, 1984, 9, 10, null, null, null, "")
    assertNull(id.displayName)
    assertNull(id.place)
    assertNull(id.lunarText)
    assertEquals("M", id.gender)
  }

  @Test
  fun noSolarDateWhenUnknown() {
    assertNull(ChartIdentityDto.of("王", Gender.M, null, null, null, null, null, null, null).birthText)
  }
}
