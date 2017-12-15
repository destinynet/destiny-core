/**
 * @author smallufo
 * Created on 2007/11/24 at 上午 2:02:13
 */
package destiny.astrology

import java.util.*
import kotlin.test.*

class AspectTest {

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
    assertEquals("合", Aspect.CONJUNCTION.toString())
    assertEquals("十二分相", Aspect.SEMISEXTILE.toString())
    assertEquals("十分相", Aspect.DECILE.toString())
    assertEquals("九分相", Aspect.NOVILE.toString())
    assertEquals("半刑", Aspect.SEMISQUARE.toString())
    assertEquals("七分相", Aspect.SEPTILE.toString())
    assertEquals("六合", Aspect.SEXTILE.toString())
    assertEquals("五分相", Aspect.QUINTILE.toString())
    assertEquals("倍九分相", Aspect.BINOVILE.toString())
    assertEquals("刑", Aspect.SQUARE.toString())
    assertEquals("倍七分相", Aspect.BISEPTILE.toString())
    assertEquals("補五分相", Aspect.SESQUIQUINTLE.toString())
    assertEquals("三合", Aspect.TRINE.toString())
    assertEquals("補八分相", Aspect.SESQUIQUADRATE.toString())
    assertEquals("倍五分相", Aspect.BIQUINTILE.toString())
    assertEquals("補十二分相", Aspect.QUINCUNX.toString())
    assertEquals("七分之三分相", Aspect.TRISEPTILE.toString())
    assertEquals("九分之四分相", Aspect.QUATRONOVILE.toString())
    assertEquals("沖", Aspect.OPPOSITION.toString())
  }

  @Test
  fun testToStringLocale() {
    val locale = Locale.ENGLISH
    assertEquals("Conjunction", Aspect.CONJUNCTION.toString(locale))
    assertEquals("SemiSextile", Aspect.SEMISEXTILE.toString(locale))
    assertEquals("Decile", Aspect.DECILE.toString(locale))
    assertEquals("Novile", Aspect.NOVILE.toString(locale))
    assertEquals("SemiSquare", Aspect.SEMISQUARE.toString(locale))
    assertEquals("Septile", Aspect.SEPTILE.toString(locale))
    assertEquals("Sextile", Aspect.SEXTILE.toString(locale))
    assertEquals("Quintile", Aspect.QUINTILE.toString(locale))
    assertEquals("BiNovile", Aspect.BINOVILE.toString(locale))
    assertEquals("Square", Aspect.SQUARE.toString(locale))
    assertEquals("BiSeptile", Aspect.BISEPTILE.toString(locale))
    assertEquals("SesquiQuintle", Aspect.SESQUIQUINTLE.toString(locale))
    assertEquals("Trine", Aspect.TRINE.toString(locale))
    assertEquals("SesquiQuadrate", Aspect.SESQUIQUADRATE.toString(locale))
    assertEquals("BiQuintile", Aspect.BIQUINTILE.toString(locale))
    assertEquals("Quincunx", Aspect.QUINCUNX.toString(locale))
    assertEquals("TriSeptile", Aspect.TRISEPTILE.toString(locale))
    assertEquals("QuatroNovile", Aspect.QUATRONOVILE.toString(locale))
    assertEquals("Opposition", Aspect.OPPOSITION.toString(locale))
  }

  @Test
  fun testGetAngles() {
    assertTrue(Aspect.getAngles(Aspect.Importance.HIGH).contains(Aspect.CONJUNCTION))
    assertTrue(Aspect.getAngles(Aspect.Importance.HIGH).contains(Aspect.SEXTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.HIGH).contains(Aspect.SQUARE))
    assertTrue(Aspect.getAngles(Aspect.Importance.HIGH).contains(Aspect.TRINE))
    assertTrue(Aspect.getAngles(Aspect.Importance.HIGH).contains(Aspect.OPPOSITION))

    assertTrue(Aspect.getAngles(Aspect.Importance.MEDIUM).contains(Aspect.SEMISEXTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.MEDIUM).contains(Aspect.SEMISQUARE))
    assertTrue(Aspect.getAngles(Aspect.Importance.MEDIUM).contains(Aspect.SESQUIQUADRATE))
    assertTrue(Aspect.getAngles(Aspect.Importance.MEDIUM).contains(Aspect.QUINCUNX))

    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.DECILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.NOVILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.SEPTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.QUINTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.BINOVILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.BISEPTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.SESQUIQUINTLE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.BIQUINTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.TRISEPTILE))
    assertTrue(Aspect.getAngles(Aspect.Importance.LOW).contains(Aspect.QUATRONOVILE))
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

}
