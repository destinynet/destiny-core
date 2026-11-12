/**
 * Created by smallufo on 2026-11-13.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LotsTest {

  private fun deg(signIndex: Int, d: Double) = (signIndex * 30 + d).toZodiacDegree()

  /**
   * 上升天蠍 3°、太陽金牛 10°、月亮巨蟹 10°；水星、火星雙子，金星牡羊，木星獅子，土星射手。
   * 日盤：福點 ＝ 213 + 100 − 40 ＝ 273（摩羯 3°），精神點 ＝ 213 + 40 − 100 ＝ 153（處女 3°）
   */
  private val h: IHoroscopeModel = mapOf<AstroPoint, ZodiacDegree>(
    Axis.RISING to deg(7, 3.0), SUN to deg(1, 10.0), MOON to deg(3, 10.0), MERCURY to deg(2, 5.0), VENUS to deg(0, 20.0),
    MARS to deg(2, 15.0), JUPITER to deg(4, 10.0), SATURN to deg(8, 10.0),
  ).mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d } }
    .let { posMap -> mockk<IHoroscopeModel> { every { positionMap } returns posMap } }

  private val impl = LotsImpl(RulerPtolemyImpl)

  @Test
  fun `日盤：福點摩羯、主星土星不相見；精神點處女、主星水星四分`() {
    val facts = impl.getFacts(h, DayNight.DAY)

    val fortune = facts.filterIsInstance<LotPlacement>().single { it.lot == Lot.Fortune }
    assertEquals(ZodiacSign.CAPRICORN, fortune.lng.sign)
    assertEquals(3, fortune.house, "摩羯在天蠍起算第 3 個")
    assertEquals(SATURN, fortune.lord)
    assertEquals(12, fortune.lordPlace, "土星射手在摩羯起算第 12 個")
    assertEquals(null, fortune.lordAspect, "第 12 個 ＝ 不相見")
    // 從摩羯起算：第 4 個牡羊（金星）、第 7 個巨蟹（月亮）；第 1、10、11 個沒有星
    assertEquals(mapOf(4 to listOf(VENUS), 7 to listOf(MOON)), fortune.planetsByPlace)

    val spirit = facts.filterIsInstance<LotPlacement>().single { it.lot == Lot.Spirit }
    assertEquals(ZodiacSign.VIRGO, spirit.lng.sign)
    assertEquals(11, spirit.house)
    assertEquals(MERCURY, spirit.lord)
    assertEquals(10, spirit.lordPlace)
    assertEquals(Aspect.SQUARE, spirit.lordAspect)
    assertEquals(mapOf(4 to listOf(SATURN), 10 to listOf(MERCURY, MARS), 11 to listOf(MOON)), spirit.planetsByPlace)

    assertEquals(listOf(LotPair(Lot.Fortune, Lot.Spirit, Aspect.TRINE)), facts.filterIsInstance<LotPair>())
  }

  @Test
  fun `夜盤對調：福點落到處女`() {
    val fortune = impl.getFacts(h, DayNight.NIGHT).filterIsInstance<LotPlacement>().single { it.lot == Lot.Fortune }
    assertEquals(ZodiacSign.VIRGO, fortune.lng.sign)
    assertEquals(LotOperands(SUN, MOON), fortune.operands, "夜盤：上升＋日−月")
  }

  @Test
  fun `Ptolemy：夜盤也是 上升＋月−日`() {
    val ptolemy = LotsImpl(RulerPtolemyImpl, LotsOptions(formula = LotOptions.PTOLEMY))
    val fortune = ptolemy.getFacts(h, DayNight.NIGHT).filterIsInstance<LotPlacement>().single { it.lot == Lot.Fortune }
    assertEquals(ZodiacSign.CAPRICORN, fortune.lng.sign)
  }

  @Test
  fun `日夜不明、沒有上升：一筆都不出`() {
    assertTrue(impl.getFacts(h, null).isEmpty())
    val noAsc = mockk<IHoroscopeModel> { every { positionMap } returns h.positionMap - Axis.RISING }
    assertTrue(impl.getFacts(noAsc, DayNight.DAY).isEmpty())
  }

  @Test
  fun `DTO：成員含 Lot、主星與宮位上的星`() {
    val dto = impl.getFacts(h, DayNight.DAY).filterIsInstance<LotPlacement>().first().toChartDto() as HoroscopeLotDto
    assertEquals(listOf(Lot.Fortune, MOON, SUN, SATURN, VENUS), dto.members)
    assertEquals(MOON to SUN, dto.plus to dto.minus, "日盤：上升＋月−日")
    assertEquals(listOf(HoroscopeLotPlaceDto(4, listOf(VENUS)), HoroscopeLotPlaceDto(7, listOf(MOON))), dto.places)
  }
}
