package destiny.core.astrology.prediction

import destiny.core.astrology.Planet.SATURN
import destiny.core.astrology.ZodiacSign.*
import destiny.core.calendar.GmtJulDay
import kotlin.test.Test
import kotlin.test.assertEquals

class ZrAngularityTest {

  /** house-distance from Lot's sign, whole-sign, Lot itself = 1 */
  @Test
  fun `signHouseFrom counts whole-sign distance with Lot as 1`() {
    assertEquals(1, signHouseFrom(CAPRICORN, CAPRICORN))   // Lot itself
    assertEquals(4, signHouseFrom(CAPRICORN, ARIES))       // 4th
    assertEquals(7, signHouseFrom(CAPRICORN, CANCER))      // 7th (opposite)
    assertEquals(10, signHouseFrom(CAPRICORN, LIBRA))      // 10th
  }

  /** modular wrap-around must be correct when Lot is late in the zodiac */
  @Test
  fun `signHouseFrom wraps around the zodiac`() {
    assertEquals(4, signHouseFrom(PISCES, GEMINI))         // Pisces->Aries(2)->Taurus(3)->Gemini(4)
    assertEquals(10, signHouseFrom(PISCES, SAGITTARIUS))   // 10th from Pisces
  }

  /** the 10th sign from the Lot is the life PEAK for that Lot's topic */
  @Test
  fun `tenth sign from Lot is PEAK`() {
    assertEquals(ZrAngularity.PEAK, zrAngularity(CAPRICORN, LIBRA))
    assertEquals(ZrAngularity.PEAK, zrAngularity(PISCES, SAGITTARIUS))
  }

  /** 1st, 4th, 7th from the Lot are angular (advancing / prominent) */
  @Test
  fun `first fourth seventh from Lot are ANGULAR`() {
    assertEquals(ZrAngularity.ANGULAR, zrAngularity(CAPRICORN, CAPRICORN)) // 1st
    assertEquals(ZrAngularity.ANGULAR, zrAngularity(CAPRICORN, ARIES))     // 4th
    assertEquals(ZrAngularity.ANGULAR, zrAngularity(CAPRICORN, CANCER))    // 7th
  }

  @Test
  fun `succedent signs are SUCCEDENT`() {
    assertEquals(ZrAngularity.SUCCEDENT, zrAngularity(CAPRICORN, AQUARIUS)) // 2nd
    assertEquals(ZrAngularity.SUCCEDENT, zrAngularity(CAPRICORN, TAURUS))   // 5th
    assertEquals(ZrAngularity.SUCCEDENT, zrAngularity(CAPRICORN, LEO))      // 8th
    assertEquals(ZrAngularity.SUCCEDENT, zrAngularity(CAPRICORN, SCORPIO))  // 11th
  }

  @Test
  fun `cadent signs are CADENT`() {
    assertEquals(ZrAngularity.CADENT, zrAngularity(CAPRICORN, PISCES))      // 3rd
    assertEquals(ZrAngularity.CADENT, zrAngularity(CAPRICORN, GEMINI))      // 6th
    assertEquals(ZrAngularity.CADENT, zrAngularity(CAPRICORN, VIRGO))       // 9th
    assertEquals(ZrAngularity.CADENT, zrAngularity(CAPRICORN, SAGITTARIUS)) // 12th
  }

  /** convenience: classify a ZodiacalReleasing period given the Lot it released from */
  @Test
  fun `angularityFrom classifies a ZR period by its sign`() {
    val period = ZodiacalReleasing(2, LIBRA, SATURN, ZrAngularity.PEAK, GmtJulDay(2440587.5), GmtJulDay(2440617.5))
    assertEquals(ZrAngularity.PEAK, period.angularityFrom(CAPRICORN))
    // the stored value is relative to the Lot it was generated from; re-reading against another Lot is free
    assertEquals(ZrAngularity.ANGULAR, period.angularityFrom(LIBRA))
  }

  /**
   * Generated periods carry [ZodiacalReleasing.angularity] measured from the **Lot**, at every
   * level — not from the immediate parent. A Capricorn Lot releasing into an L1 Aquarius whose
   * L2 sub-periods start at Aquarius: that first L2 is 2nd from the Lot (SUCCEDENT), while from
   * its own parent it would read as 1st (ANGULAR). This is the distinction that makes the
   * `lotSign` parameter of [generateSubPeriods] mandatory.
   */
  @Test
  fun `generated sub-periods measure angularity from the Lot, not the parent`() {
    val birth = GmtJulDay(2440587.5)
    val l1AquStart = GmtJulDay(birth.value + 27.0 * EGYPTIAN_YEAR_DAYS)
    val l2 = generateSubPeriods(CAPRICORN, AQUARIUS, l1AquStart, 30.0 * EGYPTIAN_YEAR_DAYS, 2)

    assertEquals(AQUARIUS, l2[0].sign)
    assertEquals(ZrAngularity.SUCCEDENT, l2[0].angularity)  // 2nd from Capricorn
    assertEquals(ZrAngularity.ANGULAR, l2[0].angularityFrom(AQUARIUS))  // 1st from its own parent

    // the 10th from the Lot is PEAK wherever it lands in the sub-cycle
    val libra = l2.first { it.sign == LIBRA }
    assertEquals(ZrAngularity.PEAK, libra.angularity)
  }

  /** L1 periods likewise: the Lot's own sign is 1st → ANGULAR, the 10th from it is PEAK */
  @Test
  fun `generated L1 periods carry angularity`() {
    val birth = GmtJulDay(2440587.5)
    val l1 = generateL1(CAPRICORN, birth, GmtJulDay(birth.value + 211 * EGYPTIAN_YEAR_DAYS))
    assertEquals(CAPRICORN, l1[0].sign)
    assertEquals(ZrAngularity.ANGULAR, l1[0].angularity)
    assertEquals(ZrAngularity.PEAK, l1.first { it.sign == LIBRA }.angularity)
  }
}
