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
    val period = ZodiacalReleasing(2, LIBRA, SATURN, GmtJulDay(2440587.5), GmtJulDay(2440617.5))
    assertEquals(ZrAngularity.PEAK, period.angularityFrom(CAPRICORN))
  }
}
