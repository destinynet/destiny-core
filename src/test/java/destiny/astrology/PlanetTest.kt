/**
 * @author smallufo
 * Created on 2007/6/12 at 上午 6:09:35
 */
package destiny.astrology


import org.junit.Assert.*
import org.junit.Test
import java.util.*

class PlanetTest {

  /** 測試從 "sun" 取得 Planet.SUN  */
  @Test
  fun testGetPlanetFromString() {

    assertSame(Planet.SUN, Planet.get("sun"))
    assertSame(Planet.SUN, Planet.get("SUN"))
    assertSame(Planet.SUN, Planet.get("Sun"))
    assertNull(Planet.get("xxx"))
  }

  /** 將 太陽 up-case 再 down-cast , 比對 equality 以及 same  */
  @Test
  fun testPlanetEqual() {
    val sun = Planet.SUN
    val sun2 = Planet.SUN

    val points = HashSet<Point>()
    points.add(sun2)

    val pointsIt = points.iterator()
    while (pointsIt.hasNext()) {
      val p = pointsIt.next()
      assertEquals("destiny.astrology.Planet", p.javaClass.name)
      if (p is Planet) {
        assertSame(p, sun)
        assertSame(p, sun)
      } else
        throw RuntimeException("Error , it should be Planet ")
    }
  }

  /** 透過 reflection 產生 太陽 , 與直接產生的太陽，比對 equality 以及 same  */
  @Test
  @Throws(Exception::class)
  fun testPlanetEqualReflection() {
    val sun = Planet.SUN

    //從 reflection 產生 太陽
    val clazz = Planet::class.java
    val list = Arrays.asList(*clazz.asSubclass(clazz).getDeclaredField("values").get(null) as Array<Star>)
    val sunInList = list[0]
    //System.out.println("比對 list 中的太陽是否相同 , hashcode = " + sunInList.hashCode() + " , " + sun.hashCode());
    assertEquals(sunInList, sun)
    assertSame(sunInList, sun)

  }

  @Test
  fun testPlanet() {
    assertEquals("太陽", Planet.SUN.getName(Locale.TAIWAN))
    assertEquals("日", Planet.SUN.getAbbreviation(Locale.TAIWAN))

    val locale = Locale.ENGLISH
    assertEquals("Sun", Planet.SUN.getName(locale))
    assertEquals("Su", Planet.SUN.getAbbreviation(locale))
  }

  @Test
  fun testPlanets() {
    for (planet in Planet.values) {
      assertNotNull(planet)
      assertNotNull(planet.toString())
    }
  }

  @Test
  fun testCompare() {
    assertTrue(Planet.SUN.compareTo(Planet.MOON) < 0)
    assertTrue(Planet.MOON.compareTo(Planet.MERCURY) < 0)
    assertTrue(Planet.MERCURY.compareTo(Planet.VENUS) < 0)
    assertTrue(Planet.VENUS.compareTo(Planet.MARS) < 0)
    assertTrue(Planet.MARS.compareTo(Planet.JUPITER) < 0)
    assertTrue(Planet.JUPITER.compareTo(Planet.SATURN) < 0)
    assertTrue(Planet.SATURN.compareTo(Planet.URANUS) < 0)
    assertTrue(Planet.URANUS.compareTo(Planet.NEPTUNE) < 0)
    assertTrue(Planet.NEPTUNE.compareTo(Planet.PLUTO) < 0)
  }

}
