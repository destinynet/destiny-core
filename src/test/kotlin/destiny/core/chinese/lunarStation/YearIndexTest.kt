/**
 * Created by smallufo on 2021-03-22.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.女
import destiny.core.astrology.LunarStation.虛
import destiny.core.chinese.StemBranch
import kotlin.test.*

internal class YearIndexTest {

  @Test
  fun testConstructor() {
    assertEquals(YearIndex(0, 1564), YearIndex(1, StemBranch.甲子, 1564))
    assertEquals(YearIndex(1, 1564), YearIndex(1, StemBranch.乙丑, 1564))
    assertEquals(YearIndex(59, 1564), YearIndex(1, StemBranch.癸亥, 1564))

    assertEquals(YearIndex(60, 1564), YearIndex(2, StemBranch.甲子, 1564))
    assertEquals(YearIndex(120, 1564), YearIndex(3, StemBranch.甲子, 1564))
    assertEquals(YearIndex(180, 1564), YearIndex(4, StemBranch.甲子, 1564))
    assertEquals(YearIndex(240, 1564), YearIndex(5, StemBranch.甲子, 1564))
    assertEquals(YearIndex(300, 1564), YearIndex(6, StemBranch.甲子, 1564))

    assertEquals(YearIndex(360, 1564), YearIndex(7, StemBranch.甲子, 1564))
    assertEquals(YearIndex(419, 1564), YearIndex(7, StemBranch.癸亥, 1564))
  }

  @Test
  fun testYearIndex() {
    assertNotNull(YearIndex(0, 1564))
    assertNotNull(YearIndex(419, 1564))

    assertNotNull(YearIndex(0, 1864))
    assertNotNull(YearIndex(419, 1864))
    try {
      YearIndex(-1, 1564)
      fail()
    } catch (e: Exception) {
      assertTrue(e is IllegalArgumentException)
    }

    try {
      YearIndex(420, 1564)
      fail()
    } catch (e: Exception) {
      assertTrue(e is IllegalArgumentException)
    }

    try {
      YearIndex(0, 2000)
      fail()
    } catch (e: Exception) {
      assertTrue(e is IllegalArgumentException)
    }
  }

  @Test
  fun testStation() {
    assertSame(虛, YearIndex(0, 1564).station)
    assertSame(女, YearIndex(419, 1564).station)

    assertSame(虛, YearIndex(0, 1864).station)
    assertSame(女, YearIndex(419, 1864).station)
  }
}
