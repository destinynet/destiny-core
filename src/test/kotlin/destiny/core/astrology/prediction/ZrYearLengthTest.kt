/**
 * Created by smallufo on 2026-11-17.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import kotlin.test.Test
import kotlin.test.assertEquals

class ZrYearLengthTest {

  private val birth = GmtJulDay(2_450_000.0)

  @Test
  fun `預設是 360 天一年`() {
    val l1 = generateL1(ZodiacSign.CANCER, birth, birth + 40 * 365.25)
    assertEquals(25 * 360.0, l1.first().toTime.value - l1.first().fromTime.value, 1e-9)
    assertEquals(ZrYearLength.VALENS_360, ZrYearLength.DEFAULT)
  }

  @Test
  fun `365_25 天一年時每一層都等比放大`() {
    val valens = generateZodiacalReleasing(ZodiacSign.CANCER, birth, birth + 40 * 365.25, maxLevel = 2)
    val julian = generateZodiacalReleasing(ZodiacSign.CANCER, birth, birth + 40 * 365.25, maxLevel = 2, yearLength = ZrYearLength.JULIAN_365_25)
    fun ZodiacalReleasing.days() = toTime.value - fromTime.value
    assertEquals(25 * 365.25, julian.first { it.level == 1 }.days(), 1e-9)
    // L2 的第一段（巨蟹 25 個月）：25 × 365.25 / 12
    val firstL2 = julian.first { it.level == 2 }
    assertEquals(ZodiacSign.CANCER, firstL2.sign)
    assertEquals(25 * 365.25 / 12, firstL2.days(), 1e-9)
    // 星座順序（含 loosing of the bond）不受年長影響
    assertEquals(valens.filter { it.level == 2 }.take(20).map { it.sign to it.isLoosingOfBond },
                 julian.filter { it.level == 2 }.take(20).map { it.sign to it.isLoosingOfBond })
  }
}
