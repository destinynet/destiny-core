/**
 * Created by smallufo on 2021-03-20.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.*
import destiny.core.chinese.StemBranch
import kotlin.test.*

internal class DayIndexTest {

  @Test
  fun testConstructor() {
    assertEquals(DayIndex(0), DayIndex(1, StemBranch.甲子))
    assertEquals(DayIndex(1), DayIndex(1, StemBranch.乙丑))
    assertEquals(DayIndex(59), DayIndex(1, StemBranch.癸亥))

    assertEquals(DayIndex(60), DayIndex(2, StemBranch.甲子))
    assertEquals(DayIndex(119), DayIndex(2, StemBranch.癸亥))

    assertEquals(DayIndex(120), DayIndex(3, StemBranch.甲子))
    assertEquals(DayIndex(179), DayIndex(3, StemBranch.癸亥))

    assertEquals(DayIndex(180), DayIndex(4, StemBranch.甲子))
    assertEquals(DayIndex(239), DayIndex(4, StemBranch.癸亥))

    assertEquals(DayIndex(240), DayIndex(5, StemBranch.甲子))
    assertEquals(DayIndex(299), DayIndex(5, StemBranch.癸亥))

    assertEquals(DayIndex(300), DayIndex(6, StemBranch.甲子))
    assertEquals(DayIndex(359), DayIndex(6, StemBranch.癸亥))

    assertEquals(DayIndex(360), DayIndex(7, StemBranch.甲子))
    assertEquals(DayIndex(419), DayIndex(7, StemBranch.癸亥))
  }

  @Test
  fun testDayIndex() {
    assertNotNull(DayIndex(0))
    assertNotNull(DayIndex(419))

    try {
      DayIndex(-1)
    } catch (e: Exception) {
      assertTrue(e is IllegalArgumentException)
      println(e)
    }

    try {
      DayIndex(420)
    } catch (e: Exception) {
      assertTrue(e is IllegalArgumentException)
      println(e)
    }
  }

  @Test
  fun testYuanAndLeader() {

    DayIndex(0).also {
      assertSame(1, it.yuan())
      assertSame(1, it.leaderIndex())
      assertSame(虛, it.leader())
      assertSame(虛, it.station())
    }

    DayIndex(1).also {
      assertSame(1, it.yuan())
      assertSame(1, it.leaderIndex())
      assertSame(虛, it.leader())
      assertSame(危, it.station())
    }

    DayIndex(14).also {
      assertSame(1, it.yuan())
      assertSame(1, it.leaderIndex())
      assertSame(虛, it.leader())
      assertSame(星, it.station())
    }

    DayIndex(15).also {
      assertSame(1, it.yuan())
      assertSame(2, it.leaderIndex())
      assertSame(張, it.leader())
      assertSame(張, it.station())
    }

    DayIndex(29).also {
      assertSame(1, it.yuan())
      assertSame(2, it.leaderIndex())
      assertSame(張, it.leader())
      assertSame(危, it.station())
    }

    DayIndex(30).also {
      assertSame(1, it.yuan())
      assertSame(3, it.leaderIndex())
      assertSame(室, it.leader())
      assertSame(室, it.station())
    }

    DayIndex(44).also {
      assertSame(1, it.yuan())
      assertSame(3, it.leaderIndex())
      assertSame(室, it.leader())
      assertSame(翼, it.station())
    }

    DayIndex(45).also {
      assertSame(1, it.yuan())
      assertSame(4, it.leaderIndex())
      assertSame(軫, it.leader())
      assertSame(軫, it.station())
    }

    DayIndex(59).also {
      assertSame(1, it.yuan())
      assertSame(4, it.leaderIndex())
      assertSame(軫, it.leader())
      assertSame(壁, it.station())
    }

    DayIndex(60).also {
      assertSame(2, it.yuan())
      assertSame(1, it.leaderIndex())
      assertSame(奎, it.leader())
      assertSame(奎, it.station())
    }

    DayIndex(360).also {
      assertSame(7, it.yuan())
      assertSame(1, it.leaderIndex())
      assertSame(箕, it.leader())
      assertSame(箕, it.station())
    }

    DayIndex(360 + 15).also {
      assertSame(7, it.yuan())
      assertSame(2, it.leaderIndex())
      assertSame(井, it.leader())
      assertSame(井, it.station())
    }

    DayIndex(360 + 15 * 2).also {
      assertSame(7, it.yuan())
      assertSame(3, it.leaderIndex())
      assertSame(牛, it.leader())
      assertSame(牛, it.station())
    }

    DayIndex(360 + 15 * 3).also {
      assertSame(7, it.yuan())
      assertSame(4, it.leaderIndex())
      assertSame(柳, it.leader())
      assertSame(柳, it.station())
    }

    DayIndex(419).also {
      assertSame(7, it.yuan())
      assertSame(4, it.leaderIndex())
      assertSame(柳, it.leader())
      assertSame(女, it.station())
    }


  }
}
