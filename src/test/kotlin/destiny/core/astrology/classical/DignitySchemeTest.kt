/**
 * Created by smallufo on 2026-11-18.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.Lang
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DignitySchemeTest {

  @Test
  fun `獅子 10° 的太陽（日盤）：廟＋三分；兩套的界主不同`() {
    val deg = 130.0.toZodiacDegree()
    val eg = DignityScheme.EGYPTIAN.of(SUN, deg, DayNight.DAY)
    assertEquals(listOf(Dignity.RULER, Dignity.TRIPLICITY), eg.own)
    assertEquals(8, eg.score)
    assertFalse(eg.peregrine)
    assertSame(VENUS, eg.lords[Dignity.TERM], "埃及界：獅子 6–11 金星")
    assertSame(JUPITER, eg.lords[Dignity.FACE], "迦勒底序：獅子第二面木星")
    assertSame(SATURN, eg.lords[Dignity.DETRIMENT])
    assertNull(eg.lords[Dignity.EXALTATION], "獅子沒有旺")

    assertSame(MERCURY, DignityScheme.PTOLEMAIC.lordOf(Dignity.TERM, deg, DayNight.DAY), "托勒密界：獅子 6–13 水星")
  }

  @Test
  fun `雙魚 27° 的金星（日盤）：Dorotheus 三分給金星，托勒密三分給火星`() {
    val deg = 357.0.toZodiacDegree()
    val eg = DignityScheme.EGYPTIAN.of(VENUS, deg, DayNight.DAY)
    assertEquals(listOf(Dignity.EXALTATION, Dignity.TRIPLICITY), eg.own)
    assertEquals(7, eg.score)
    assertSame(MARS, eg.lords[Dignity.TERM], "埃及界：雙魚 19–28 火星")

    val pt = DignityScheme.PTOLEMAIC.of(VENUS, deg, DayNight.DAY)
    assertEquals(listOf(Dignity.EXALTATION), pt.own)
    assertEquals(4, pt.score)
    assertSame(MARS, pt.lords[Dignity.TRIPLICITY])
    assertSame(SATURN, pt.lords[Dignity.TERM], "托勒密界：雙魚 26–30 土星")
  }

  @Test
  fun `外來扣 5 分；陷與面相抵`() {
    val mars = DignityScheme.EGYPTIAN.of(MARS, 65.0.toZodiacDegree(), DayNight.DAY)
    assertTrue(mars.peregrine)
    assertEquals(-5, mars.score)

    val saturn = DignityScheme.EGYPTIAN.of(SATURN, 121.0.toZodiacDegree(), DayNight.DAY)
    assertEquals(listOf(Dignity.FACE, Dignity.DETRIMENT), saturn.own)
    assertFalse(saturn.peregrine, "落陷不算外來（與 Debility.Peregrine 同一個判準）")
    assertEquals(-4, saturn.score)
  }

  @Test
  fun `日夜不明：三分主為 null`() {
    assertNull(DignityScheme.EGYPTIAN.lordOf(Dignity.TRIPLICITY, 130.0.toZodiacDegree(), null))
  }

  @Test
  fun `區分角色：日盤與夜盤對調`() {
    val day = Sect(DayNight.DAY, SUN, listOf(SUN, MERCURY, JUPITER, SATURN), listOf(MOON, VENUS, MARS), JUPITER, MARS, DayNight.DAY)
    assertEquals(SectRole.LIGHT_OF_SECT, day.roleOf(SUN))
    assertEquals(SectRole.LIGHT_CONTRARY, day.roleOf(MOON))
    assertEquals(SectRole.BENEFIC_OF_SECT, day.roleOf(JUPITER))
    assertEquals(SectRole.BENEFIC_CONTRARY, day.roleOf(VENUS))
    assertEquals(SectRole.MALEFIC_OF_SECT, day.roleOf(SATURN))
    assertEquals(SectRole.MALEFIC_CONTRARY, day.roleOf(MARS))
    assertEquals(SectRole.MERCURY_OF_SECT, day.roleOf(MERCURY))

    val night = Sect(DayNight.NIGHT, MOON, listOf(MOON, VENUS, MARS), listOf(SUN, MERCURY, JUPITER, SATURN), VENUS, SATURN, DayNight.DAY)
    assertEquals(SectRole.LIGHT_OF_SECT, night.roleOf(MOON))
    assertEquals(SectRole.BENEFIC_OF_SECT, night.roleOf(VENUS))
    assertEquals(SectRole.BENEFIC_CONTRARY, night.roleOf(JUPITER))
    assertEquals(SectRole.MALEFIC_OF_SECT, night.roleOf(MARS))
    assertEquals(SectRole.MALEFIC_CONTRARY, night.roleOf(SATURN))
    assertEquals(SectRole.MERCURY_CONTRARY, night.roleOf(MERCURY))
  }

  @Test
  fun `顯示名`() {
    assertEquals("區分內吉星", SectRole.BENEFIC_OF_SECT.getTitle(Lang.ZH_TW))
    assertEquals("Malefic contrary to sect", SectRole.MALEFIC_CONTRARY.getTitle(Lang.EN))
    DignityScheme.entries.forEach { s -> listOf(Lang.ZH_TW, Lang.EN).forEach { assertTrue(s.getTitle(it) != s.name) } }
    SectRole.entries.forEach { r -> listOf(Lang.ZH_TW, Lang.EN).forEach { assertTrue(r.getTitle(it) != r.name) } }
  }
}
