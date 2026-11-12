/**
 * Created by smallufo on 2026-11-13.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LotFormulaTest {

  private val lngs: Map<AstroPoint, ZodiacDegree> = mapOf(
    Axis.RISING to 100.0, Planet.SUN to 20.0, Planet.MOON to 50.0, Planet.VENUS to 200.0, Planet.SATURN to 300.0,
  ).mapValues { it.value.toZodiacDegree() }

  private fun lot(a: Lot, sect: DayNight, options: LotOptions = LotOptions.DEFAULT) = a.lngOf(lngs, sect, options)?.value

  /** 福點：日盤 上升＋月−日，夜盤 上升＋日−月 */
  @Test
  fun fortune() {
    assertEquals(130.0, lot(Lot.Fortune, DayNight.DAY))
    assertEquals(70.0, lot(Lot.Fortune, DayNight.NIGHT))
  }

  /** 精神點與福點互為鏡像 */
  @Test
  fun spirit() {
    assertEquals(70.0, lot(Lot.Spirit, DayNight.DAY))
    assertEquals(130.0, lot(Lot.Spirit, DayNight.NIGHT))
  }

  /** Ptolemy：夜盤不對調 */
  @Test
  fun ptolemyDoesNotReverse() {
    assertEquals(130.0, lot(Lot.Fortune, DayNight.NIGHT, LotOptions.PTOLEMY))
    assertEquals(70.0, lot(Lot.Spirit, DayNight.NIGHT, LotOptions.PTOLEMY))
  }

  /** 運算元是 Lot 時遞迴：愛情點用精神點、復仇點用福點 */
  @Test
  fun recursive() {
    // 日：100 + 金 200 − 精神 70
    assertEquals(230.0, lot(Lot.Eros, DayNight.DAY))
    // 夜：100 + 精神 130 − 金 200 ＝ 30
    assertEquals(30.0, lot(Lot.Eros, DayNight.NIGHT))
    // 日：100 + 福 130 − 土 300 ＝ −70 → 290
    assertEquals(290.0, lot(Lot.Nemesis, DayNight.DAY))
  }

  /** 缺點就回 null，不猜 */
  @Test
  fun missingPoint() {
    assertNull(lot(Lot.Victory, DayNight.DAY))
    assertNull(Lot.Fortune.lngOf(lngs - Axis.RISING, DayNight.DAY))
  }

  /** 七個 Lot 都有公式，且運算元不是上升本身 */
  @Test
  fun everyLotHasOperands() {
    Lot.values.forEach { a ->
      val (plus, minus) = a.operands
      assert(plus != minus && plus != Axis.RISING && minus != Axis.RISING) { a }
    }
  }
}
