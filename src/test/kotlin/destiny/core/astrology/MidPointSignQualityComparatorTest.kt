/**
 * Created by smallufo on 2024-11-04.
 */
package destiny.core.astrology

import destiny.core.astrology.Planet.MOON
import destiny.core.astrology.Planet.SUN
import kotlin.test.Test
import kotlin.test.assertEquals

class MidPointSignQualityComparatorTest {

  @Test
  fun testSort() {
    val mp1 = MidPoint(setOf(SUN, MOON), ZodiacDegree.of(ZodiacSign.CANCER, 5.0), 1)    // Cardinal, degree 5.0
    val mp2 = MidPoint(setOf(SUN, MOON), ZodiacDegree.of(ZodiacSign.CAPRICORN, 7.0), 1) // Cardinal, degree 7.0
    val mp3 = MidPoint(setOf(SUN, MOON), ZodiacDegree.of(ZodiacSign.LIBRA, 10.0), 1)    // Cardinal, degree 10.0
    val mp4 = MidPoint(setOf(SUN, MOON), ZodiacDegree.of(ZodiacSign.TAURUS, 15.0), 1)   // Fixed, degree 15.0
    val mp5 = MidPoint(setOf(SUN, MOON), ZodiacDegree.of(ZodiacSign.GEMINI, 20.0), 1)   // Mutable, degree 20.0

    val expectedOrder = listOf(mp1, mp2, mp3, mp4, mp5)

    repeat(100) {
      val actual = expectedOrder.shuffled().sortedWith(MidPointSignQualityComparator)
      assertEquals(expectedOrder, actual)
    }
  }
}
