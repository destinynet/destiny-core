/**
 * Created by smallufo on 2026-01-19.
 * Egyptian Terms (Bounds) Implementation
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Planet.*
import destiny.core.astrology.PointDegree
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign
import java.io.Serializable

/**
 * Egyptian Terms (Bounds) - 埃及界限
 *
 * This is the traditional Egyptian bounds system, different from Ptolemy's bounds.
 * Each zodiac sign is divided into 5 unequal parts (terms), each ruled by one of the
 * 5 visible planets (excluding Sun and Moon).
 */
object TermEgyptianImpl : ITerm, Serializable {
  private fun readResolve(): Any = TermEgyptianImpl

  override fun getPoint(degree: ZodiacDegree): AstroPoint {
    val signIndex = degree.value.toInt() / 30

    return (0..4)
      .map { termPointDegrees[signIndex * 5 + it] }
      .filter { degree.value < it.degree }
      .map { it.point }
      .firstOrNull() ?: throw IllegalStateException("Cannot find Egyptian Terms at degree $degree , signIndex = $signIndex")
  }

  override fun ZodiacSign.getTermPoint(degree: Double): AstroPoint {
    return getPoint((this.degree + degree).toZodiacDegree())
  }

  override fun getTermBound(degree: ZodiacDegree): TermBound {
    val signIndex = degree.value.toInt() / 30
    val signStartDegree = signIndex * 30.0

    // Find the term index within this sign (0..4)
    val termIndex = (0..4).first { i ->
      degree.value < termPointDegrees[signIndex * 5 + i].degree
    }

    val toDegree = termPointDegrees[signIndex * 5 + termIndex].degree.toZodiacDegree()
    val fromDegree = if (termIndex == 0) {
      signStartDegree.toZodiacDegree()
    } else {
      termPointDegrees[signIndex * 5 + termIndex - 1].degree.toZodiacDegree()
    }
    val ruler = termPointDegrees[signIndex * 5 + termIndex].point

    return TermBound(ruler, fromDegree, toDegree)
  }

  /**
   * Egyptian Terms Table
   * Degrees are cumulative (end degree of each term)
   *
   * Reference: Vettius Valens, Paulus Alexandrinus, and other Hellenistic sources
   */
  private val termPointDegrees = listOf(
    // Aries (0-30): Jupiter 0-6, Venus 6-12, Mercury 12-20, Mars 20-25, Saturn 25-30
    PointDegree(JUPITER, 6.0),
    PointDegree(VENUS, 12.0),
    PointDegree(MERCURY, 20.0),
    PointDegree(MARS, 25.0),
    PointDegree(SATURN, 30.0),

    // Taurus (30-60): Venus 0-8, Mercury 8-14, Jupiter 14-22, Saturn 22-27, Mars 27-30
    PointDegree(VENUS, 38.0),
    PointDegree(MERCURY, 44.0),
    PointDegree(JUPITER, 52.0),
    PointDegree(SATURN, 57.0),
    PointDegree(MARS, 60.0),

    // Gemini (60-90): Mercury 0-6, Jupiter 6-12, Venus 12-17, Mars 17-24, Saturn 24-30
    PointDegree(MERCURY, 66.0),
    PointDegree(JUPITER, 72.0),
    PointDegree(VENUS, 77.0),
    PointDegree(MARS, 84.0),
    PointDegree(SATURN, 90.0),

    // Cancer (90-120): Mars 0-7, Venus 7-13, Mercury 13-19, Jupiter 19-26, Saturn 26-30
    PointDegree(MARS, 97.0),
    PointDegree(VENUS, 103.0),
    PointDegree(MERCURY, 109.0),
    PointDegree(JUPITER, 116.0),
    PointDegree(SATURN, 120.0),

    // Leo (120-150): Jupiter 0-6, Venus 6-11, Saturn 11-18, Mercury 18-24, Mars 24-30
    PointDegree(JUPITER, 126.0),
    PointDegree(VENUS, 131.0),
    PointDegree(SATURN, 138.0),
    PointDegree(MERCURY, 144.0),
    PointDegree(MARS, 150.0),

    // Virgo (150-180): Mercury 0-7, Venus 7-17, Jupiter 17-21, Mars 21-28, Saturn 28-30
    PointDegree(MERCURY, 157.0),
    PointDegree(VENUS, 167.0),
    PointDegree(JUPITER, 171.0),
    PointDegree(MARS, 178.0),
    PointDegree(SATURN, 180.0),

    // Libra (180-210): Saturn 0-6, Mercury 6-14, Jupiter 14-21, Venus 21-28, Mars 28-30
    PointDegree(SATURN, 186.0),
    PointDegree(MERCURY, 194.0),
    PointDegree(JUPITER, 201.0),
    PointDegree(VENUS, 208.0),
    PointDegree(MARS, 210.0),

    // Scorpio (210-240): Mars 0-7, Venus 7-11, Mercury 11-19, Jupiter 19-24, Saturn 24-30
    PointDegree(MARS, 217.0),
    PointDegree(VENUS, 221.0),
    PointDegree(MERCURY, 229.0),
    PointDegree(JUPITER, 234.0),
    PointDegree(SATURN, 240.0),

    // Sagittarius (240-270): Jupiter 0-12, Venus 12-17, Mercury 17-21, Saturn 21-26, Mars 26-30
    PointDegree(JUPITER, 252.0),
    PointDegree(VENUS, 257.0),
    PointDegree(MERCURY, 261.0),
    PointDegree(SATURN, 266.0),
    PointDegree(MARS, 270.0),

    // Capricorn (270-300): Mercury 0-7, Jupiter 7-14, Venus 14-22, Saturn 22-26, Mars 26-30
    PointDegree(MERCURY, 277.0),
    PointDegree(JUPITER, 284.0),
    PointDegree(VENUS, 292.0),
    PointDegree(SATURN, 296.0),
    PointDegree(MARS, 300.0),

    // Aquarius (300-330): Mercury 0-7, Venus 7-13, Jupiter 13-20, Mars 20-25, Saturn 25-30
    PointDegree(MERCURY, 307.0),
    PointDegree(VENUS, 313.0),
    PointDegree(JUPITER, 320.0),
    PointDegree(MARS, 325.0),
    PointDegree(SATURN, 330.0),

    // Pisces (330-360): Venus 0-12, Jupiter 12-16, Mercury 16-19, Mars 19-28, Saturn 28-30
    PointDegree(VENUS, 342.0),
    PointDegree(JUPITER, 346.0),
    PointDegree(MERCURY, 349.0),
    PointDegree(MARS, 358.0),
    PointDegree(SATURN, 359.999999999999) // Avoid normalize to 0
  )
}
