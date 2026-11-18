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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AlmutenTest {

  /**
   * 日盤、埃及界：
   * - 牡羊 15°：廟火星 5、旺太陽 4、三分（火象日）太陽 3、界（12–20）水星 2、面（10–20）太陽 1 ⇒ 太陽 8、火星 5、水星 2
   * - 獅子 10°：廟太陽 5、三分太陽 3、界（6–11）金星 2、面（10–20）木星 1 ⇒ 太陽 8、金星 2、木星 1
   */
  @Test
  fun `五點加總：太陽在牡羊、其餘四點在獅子`() {
    val leo = 130.0.toZodiacDegree()
    val r = AlmutenOptions.IBN_EZRA.score(
      mapOf(AlmutenPoint.SUN to 15.0.toZodiacDegree(), AlmutenPoint.MOON to leo, AlmutenPoint.ASCENDANT to leo, AlmutenPoint.FORTUNE to leo, AlmutenPoint.SYZYGY to leo),
      DayNight.DAY,
    )
    assertEquals(listOf(SUN), r.winners)
    assertEquals(SUN to 40, r.totals.first())
    assertEquals(8, r.points.first { it.point == AlmutenPoint.SUN }.scoreOf(SUN))
    assertEquals(listOf(Dignity.EXALTATION, Dignity.TRIPLICITY, Dignity.FACE), r.points.first().awards[SUN])
    assertEquals(mapOf(SUN to 40, VENUS to 8, JUPITER to 4, MARS to 5, MERCURY to 2), r.totals.filter { it.second > 0 }.toMap())
    assertEquals(7, r.totals.size, "七顆星都列，沒分的是 0")
  }

  /** 天秤 15°（日盤）：廟金星 5、旺土星 4、三分（風象日）土星 3、界（14–21）木星 2、面（10–20）土星 1 ⇒ 土星 8，與牡羊 15° 的太陽 8 同分 */
  @Test
  fun `同分時列出全部，依古典行星順序`() {
    val r = AlmutenOptions(points = listOf(AlmutenPoint.SUN, AlmutenPoint.MOON))
      .score(mapOf(AlmutenPoint.SUN to 15.0.toZodiacDegree(), AlmutenPoint.MOON to 195.0.toZodiacDegree()), DayNight.DAY)
    assertEquals(listOf(SUN, SATURN), r.winners)
  }

  @Test
  fun `缺的點略過；落陷不能計分`() {
    val r = AlmutenOptions.IBN_EZRA.score(mapOf(AlmutenPoint.ASCENDANT to 130.0.toZodiacDegree()), DayNight.DAY)
    assertEquals(1, r.points.size)
    assertFailsWith<IllegalArgumentException> { AlmutenOptions(dignities = listOf(Dignity.RULER, Dignity.FALL)) }
  }

  @Test
  fun `托勒密界：獅子 10° 的界主是水星`() {
    val r = AlmutenOptions(points = listOf(AlmutenPoint.ASCENDANT), scheme = DignityScheme.PTOLEMAIC)
      .score(mapOf(AlmutenPoint.ASCENDANT to 130.0.toZodiacDegree()), DayNight.DAY)
    assertEquals(listOf(Dignity.TERM), r.points.single().awards[MERCURY])
  }

  @Test
  fun `顯示名`() {
    AlmutenPoint.entries.forEach { p -> listOf(Lang.ZH_TW, Lang.EN).forEach { assertTrue(p.getTitle(it) != p.name) } }
    assertEquals("Prenatal syzygy", AlmutenPoint.SYZYGY.getTitle(Lang.EN))
  }
}
