/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 6:09:35
 */
package destiny.astrology


import java.util.*
import kotlin.test.*

class PlanetTest {

  /** 測試從 "sun" 取得 Planet.SUN  */
  @Test
  fun testGetPlanetFromString() {

    assertSame(Planet.MOON, Planet.fromString("MOON"))

    assertSame(Planet.SUN, Planet.fromString("sun"))
    assertSame(Planet.SUN, Planet.fromString("SUN"))
    assertSame(Planet.SUN, Planet.fromString("Sun"))
    assertNull(Planet.fromString("xxx"))
  }

  /** 將 太陽 up-case 再 down-cast , 比對 equality 以及 same  */
  @Test
  fun testPlanetEqual() {
    val sun1 = Planet.SUN
    val sun2 = Planet.SUN

    val points = setOf<Point>(sun2)

    val pointsIt = points.iterator()
    while (pointsIt.hasNext()) {
      val p = pointsIt.next()

      if (p is Planet) {
        assertSame(p, sun1)
        assertSame(p, sun1)
      } else
        throw RuntimeException("Error , it should be Planet ")
    }
  }


  @Test
  fun testPlanet() {
    assertEquals("太陽", Planet.SUN.toString(Locale.TAIWAN))
    assertEquals("太阳", Planet.SUN.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Sun", Planet.SUN.toString(Locale.ENGLISH))

    assertEquals("日", Planet.SUN.getAbbreviation(Locale.TAIWAN))

    val locale = Locale.ENGLISH
    assertEquals("Sun", Planet.SUN.toString(locale))
    assertEquals("Su", Planet.SUN.getAbbreviation(locale))
  }

  @Test
  fun testPlanets() {

    for (planet in Planet.array) {
      assertNotNull(planet)
      assertNotNull(planet.toString(Locale.TAIWAN))
    }

    val points = setOf<Point>(*Planet.array, *FixedStar.array) as Collection<Point>
    println(points)
  }

  @Test
  fun testCompare() {
    assertTrue(Planet.SUN < Planet.MOON)
    assertTrue(Planet.MOON < Planet.MERCURY)
    assertTrue(Planet.MERCURY < Planet.VENUS)
    assertTrue(Planet.VENUS < Planet.MARS)
    assertTrue(Planet.MARS < Planet.JUPITER)
    assertTrue(Planet.JUPITER < Planet.SATURN)
    assertTrue(Planet.SATURN < Planet.URANUS)
    assertTrue(Planet.URANUS < Planet.NEPTUNE)
    assertTrue(Planet.NEPTUNE < Planet.PLUTO)
  }

}
