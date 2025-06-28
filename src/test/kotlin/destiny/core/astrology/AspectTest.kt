/**
 * @author smallufo
 * Created on 2007/11/24 at 上午 2:02:13
 */
package destiny.core.astrology

import destiny.core.EnumTest
import destiny.core.astrology.Aspect.Companion.expand
import destiny.core.astrology.Aspect.Companion.expandMulti
import destiny.tools.getTitle
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.test.*

class AspectTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Aspect::class)
  }

  @Test
  fun testGetAspectFromString() {
    assertSame(Aspect.CONJUNCTION, Aspect.getAspectFromName("Conjunction"))
    assertSame(Aspect.OPPOSITION, Aspect.getAspectFromName("Opposition"))
    assertSame(Aspect.SQUARE, Aspect.getAspectFromName("SQUARE"))
    assertSame(Aspect.TRINE, Aspect.getAspectFromName("TrInE"))
    assertSame(Aspect.SEXTILE, Aspect.getAspectFromName("sextile"))
    assertSame(Aspect.SEXTILE, Aspect.getAspectFromName("\t\nsextile \t "))
    assertNull(Aspect.getAspectFromName("xxx"))
  }

  @Test
  fun testToString() {
    assertEquals("合", Aspect.CONJUNCTION.getTitle(Locale.TAIWAN))
    assertEquals("十二分相", Aspect.SEMISEXTILE.getTitle(Locale.TAIWAN))
    assertEquals("十分相", Aspect.DECILE.getTitle(Locale.TAIWAN))
    assertEquals("九分相", Aspect.NOVILE.getTitle(Locale.TAIWAN))
    assertEquals("半刑", Aspect.SEMISQUARE.getTitle(Locale.TAIWAN))
    assertEquals("七分相", Aspect.SEPTILE.getTitle(Locale.TAIWAN))
    assertEquals("六合", Aspect.SEXTILE.getTitle(Locale.TAIWAN))
    assertEquals("五分相", Aspect.QUINTILE.getTitle(Locale.TAIWAN))
    assertEquals("倍九分相", Aspect.BINOVILE.getTitle(Locale.TAIWAN))
    assertEquals("刑", Aspect.SQUARE.getTitle(Locale.TAIWAN))
    assertEquals("倍七分相", Aspect.BISEPTILE.getTitle(Locale.TAIWAN))
    assertEquals("補五分相", Aspect.SESQUIQUINTLE.getTitle(Locale.TAIWAN))
    assertEquals("三合", Aspect.TRINE.getTitle(Locale.TAIWAN))
    assertEquals("補八分相", Aspect.SESQUIQUADRATE.getTitle(Locale.TAIWAN))
    assertEquals("倍五分相", Aspect.BIQUINTILE.getTitle(Locale.TAIWAN))
    assertEquals("補十二分相", Aspect.QUINCUNX.getTitle(Locale.TAIWAN))
    assertEquals("七分之三分相", Aspect.TRISEPTILE.getTitle(Locale.TAIWAN))
    assertEquals("九分之四分相", Aspect.QUATRONOVILE.getTitle(Locale.TAIWAN))
    assertEquals("沖", Aspect.OPPOSITION.getTitle(Locale.TAIWAN))
  }

  @Test
  fun testToStringLocale() {
    val locale = Locale.ENGLISH
    assertEquals("Conjunction", Aspect.CONJUNCTION.getTitle(locale))
    assertEquals("SemiSextile", Aspect.SEMISEXTILE.getTitle(locale))
    assertEquals("Decile", Aspect.DECILE.getTitle(locale))
    assertEquals("Novile", Aspect.NOVILE.getTitle(locale))
    assertEquals("SemiSquare", Aspect.SEMISQUARE.getTitle(locale))
    assertEquals("Septile", Aspect.SEPTILE.getTitle(locale))
    assertEquals("Sextile", Aspect.SEXTILE.getTitle(locale))
    assertEquals("Quintile", Aspect.QUINTILE.getTitle(locale))
    assertEquals("BiNovile", Aspect.BINOVILE.getTitle(locale))
    assertEquals("Square", Aspect.SQUARE.getTitle(locale))
    assertEquals("BiSeptile", Aspect.BISEPTILE.getTitle(locale))
    assertEquals("SesquiQuintile", Aspect.SESQUIQUINTLE.getTitle(locale))
    assertEquals("Trine", Aspect.TRINE.getTitle(locale))
    assertEquals("SesquiQuadrate", Aspect.SESQUIQUADRATE.getTitle(locale))
    assertEquals("BiQuintile", Aspect.BIQUINTILE.getTitle(locale))
    assertEquals("Quincunx", Aspect.QUINCUNX.getTitle(locale))
    assertEquals("TriSeptile", Aspect.TRISEPTILE.getTitle(locale))
    assertEquals("QuatroNovile", Aspect.QUATRONOVILE.getTitle(locale))
    assertEquals("Opposition", Aspect.OPPOSITION.getTitle(locale))
  }

  @Test
  fun testGetAngles() {
    assertTrue(Aspect.getAspects(Aspect.Importance.HIGH).contains(Aspect.CONJUNCTION))
    assertTrue(Aspect.getAspects(Aspect.Importance.HIGH).contains(Aspect.SEXTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.HIGH).contains(Aspect.SQUARE))
    assertTrue(Aspect.getAspects(Aspect.Importance.HIGH).contains(Aspect.TRINE))
    assertTrue(Aspect.getAspects(Aspect.Importance.HIGH).contains(Aspect.OPPOSITION))

    assertTrue(Aspect.getAspects(Aspect.Importance.MEDIUM).contains(Aspect.SEMISEXTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.MEDIUM).contains(Aspect.SEMISQUARE))
    assertTrue(Aspect.getAspects(Aspect.Importance.MEDIUM).contains(Aspect.SESQUIQUADRATE))
    assertTrue(Aspect.getAspects(Aspect.Importance.MEDIUM).contains(Aspect.QUINCUNX))

    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.DECILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.NOVILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.SEPTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.QUINTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.BINOVILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.BISEPTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.SESQUIQUINTLE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.BIQUINTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.TRISEPTILE))
    assertTrue(Aspect.getAspects(Aspect.Importance.LOW).contains(Aspect.QUATRONOVILE))
  }

  @Test
  fun testGetAspect() {
    assertSame(Aspect.getAspect(0.0), Aspect.CONJUNCTION)
    assertSame(Aspect.getAspect(60.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(90.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(120.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(150.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(180.0), Aspect.OPPOSITION)
    assertSame(Aspect.getAspect(210.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(240.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(270.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(300.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(360.0), Aspect.CONJUNCTION)

    assertSame(Aspect.getAspect(360.0), Aspect.CONJUNCTION)
    assertSame(Aspect.getAspect(420.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(450.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(480.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(510.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(540.0), Aspect.OPPOSITION)
    assertSame(Aspect.getAspect(570.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(600.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(630.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(660.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(720.0), Aspect.CONJUNCTION)


    assertSame(Aspect.getAspect(0.0), Aspect.CONJUNCTION)
    assertSame(Aspect.getAspect(-60.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(-90.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(-120.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(-150.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(-180.0), Aspect.OPPOSITION)
    assertSame(Aspect.getAspect(-210.0), Aspect.QUINCUNX)
    assertSame(Aspect.getAspect(-240.0), Aspect.TRINE)
    assertSame(Aspect.getAspect(-270.0), Aspect.SQUARE)
    assertSame(Aspect.getAspect(-300.0), Aspect.SEXTILE)
    assertSame(Aspect.getAspect(-360.0), Aspect.CONJUNCTION)
  }

  @Nested
  inner class ExpandMultiTest {
    @Test
    fun `single aspect should expand correctly`() {
      val aspects = setOf(Aspect.SQUARE)
      val expanded = aspects.expandMulti()

      val expectedDegrees = setOf(0.0, 90.0, 180.0, 270.0)
      assertEquals(expectedDegrees, expanded.keys.toSet())

      expectedDegrees.forEach {
        assertTrue(
          expanded[it]?.contains(Aspect.SQUARE) == true,
          "Expected SQUARE to appear at $it"
        )
      }
    }

    @Test
    fun `multiple aspects should not overwrite each other`() {
      val aspects = setOf(Aspect.CONJUNCTION, Aspect.OPPOSITION)
      val expanded = aspects.expandMulti()

      // CONJUNCTION = 0.0
      // OPPOSITION = 180.0, 0.0 (due to 180 * 2 = 360 % 360 = 0)
      val zeroDegreeAspects = expanded[0.0] ?: emptyList()
      val oneEightyAspects = expanded[180.0] ?: emptyList()

      assertTrue(zeroDegreeAspects.containsAll(listOf(Aspect.CONJUNCTION, Aspect.OPPOSITION)))
      assertTrue(oneEightyAspects.contains(Aspect.OPPOSITION))
    }

    @Test
    fun `expand should include septile with non-integer degrees`() {
      val aspects = setOf(Aspect.SEPTILE)
      val expanded = aspects.expandMulti()

      val expected = List(7) { i -> (i * Aspect.SEPTILE.degree) % 360 }

      expected.forEach { degree ->
        val matched = expanded.keys.any { kotlin.math.abs(it - degree) < 1e-6 }
        assertTrue(matched, "Expected SEPTILE at approx $degree°")
      }
    }

    @Test
    fun `expand full high importance aspects`() {
      val aspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()
      val expanded = aspects.expandMulti()

      val expectedDegrees = setOf(0.0, 60.0, 90.0, 120.0, 180.0, 240.0, 270.0, 300.0)

      expectedDegrees.forEach {
        assertTrue(
          expanded.containsKey(it),
          "Expected high importance aspect at $it°"
        )
      }
    }

    @Test
    fun `no aspect returns empty map`() {
      val expanded = emptySet<Aspect>().expandMulti()
      assertTrue(expanded.isEmpty())
    }
  }

  @Test
  fun testExpand() {
    Aspect.getAspects(Aspect.Importance.HIGH).toSet().expand().forEach { (deg, aspect) ->
      println("$deg -> $aspect")

    }
  }

}
